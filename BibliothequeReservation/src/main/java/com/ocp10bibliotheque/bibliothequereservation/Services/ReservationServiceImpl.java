package com.ocp10bibliotheque.bibliothequereservation.Services;

import com.ocp10bibliotheque.bibliothequereservation.DAO.BookRepository;
import com.ocp10bibliotheque.bibliothequereservation.DAO.LendingRepository;
import com.ocp10bibliotheque.bibliothequereservation.DAO.ReservationRepository;
import com.ocp10bibliotheque.bibliothequereservation.DAO.UserAccountRepository;
import com.ocp10bibliotheque.bibliothequereservation.Entites.*;
import com.ocp10bibliotheque.bibliothequereservation.Mail.MyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


@Service
@Transactional
public class ReservationServiceImpl implements IReservationService{

    @Autowired
    BookRepository bookRepository;

    @Autowired
    public JavaMailSender emailSender;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(465);

        mailSender.setUsername(MyConstants.MY_EMAIL);
        mailSender.setPassword(MyConstants.MY_PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Autowired
    LendingRepository lendingRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    ReservationRepository reservationRepository;
    @Override
    public Reservation reserve(String userAccountMail, int idBook) throws Exception {
        Optional<Book> book = bookRepository.findById(idBook);
        if(book.isEmpty()) throw new Exception ("Ce livre n'existe pas !");
        Optional<UserAccount> userAccount = userAccountRepository.findByMail(userAccountMail);
        if(userAccount.isEmpty()) throw new Exception ("Cet utilisateur n'existe pas !");
        //vérifier si le nombre max de réservations pour cet ouvrage est atteint
        if((book.get().getNumberExemplarTotal()*2)<=book.get().getCurrentNumberReservation()) throw new Exception("Le nombre maximum de réservations est atteint !");
        //vérifier que le livre n'est pas emprunté par cet utilisateur.
        List<Lending> lendingList = lendingRepository.findByBookAndUserAccount(book.get(),userAccount.get());
        for (Lending lending:lendingList){
            if(!lending.getStatus().equals("Terminé"))throw new Exception("Cet utilisateur ne peut pas faire une réservation car un prêt est en cours pour cet ouvrage !");
        }
        //vérifier que cet utilisateur n'as pas déjà une réservation en cours pour cet ouvrage
        List<Reservation> listReservation = reservationRepository.findByBookAndUserAccount(book.get(),userAccount.get());
        for (Reservation reservationFromList: listReservation){
            if(reservationFromList.getStatus().equals("en attente") || reservationFromList.getStatus().equals("en cours"))
                throw new Exception("Une réservation est déjà en cours pour cet utilisateur !");
        }
        //Création de la réservation
        Reservation reservation = new Reservation(userAccount.get(),book.get(),book.get().getLibrary());
        //on vérifie si c'est la première réservation pour cet ouvrage: si oui on envoi le mail sinon status = "en attente"
       List<Reservation> listReservation2 = reservationRepository.findByBook(book.get());
       boolean check = false;
        for (Reservation reservationFromList2: listReservation2){
            if(!reservationFromList2.getStatus().equals("Terminée")) check = true;
        }
        if (check==true) reservation.setStatus("en attente");
        else{
            reservation.setStatus("en cours");
            // on envoie le mail
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userAccount.get().getMail());
            message.setSubject("Reservaton ce l'ouvrage: "+book.get().getTitle());
            message.setText("Bonjour, l'ouvrage "+book.get().getTitle()+" a été réservé le " +reservation.getStartDate()+" à la librairie: "
                    +reservation.getLibrary().getName() + " ce dernier sera disponible pendant 48h.");
            this.emailSender.send(message);
            reservation.setMailIsSend(true);
            reservation.setStartDate(LocalDateTime.now());
            reservation.setEndDate(reservation.getStartDate().plusDays(2));
        }
        reservation.getBook().setCurrentNumberReservation(reservation.getBook().getCurrentNumberReservation()+1);
        bookRepository.saveAndFlush(reservation.getBook());
        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> displayReservationByUser(String userAccountMail) throws Exception {
        Optional<UserAccount> userAccount = userAccountRepository.findByMail(userAccountMail);
        if(userAccount.isEmpty()) throw new Exception ("Cet utilisateur n'existe pas !");
        for (Role role:userAccount.get().getRoles()
        ) {
            if (role.getName().equals("ADMIN") || role.getName().equals("EMPLOYEE")) return reservationRepository.findAll();
        }
        return reservationRepository.findByUserAccount(userAccount.get());
    }

    @Override
    public void cancelReservation(int idReservation) throws Exception {
        Optional<Reservation> reservation = reservationRepository.findById(idReservation);
        if (reservation.isEmpty()) throw new Exception("Cette réservation n'existe pas !");
        reservation.get().setStatus("Annulée");
        reservation.get().getBook().setCurrentNumberReservation(reservation.get().getBook().getCurrentNumberReservation()-1);
        reservationRepository.saveAndFlush(reservation.get());
    }

    @Override
    //Only for ADMIN  users.
    public void deleteReservation(int idReservation) throws Exception {
        Optional<Reservation> reservation = reservationRepository.findById(idReservation);
        if (reservation.isEmpty()) throw new Exception("Cette réservation n'existe pas !");
        reservation.get().getBook().setCurrentNumberReservation(reservation.get().getBook().getCurrentNumberReservation()-1);
        reservationRepository.delete(reservation.get());
    }

    @Override
    //Only for EMPLOYEE  users.
    public void validateReservation(int idReservation) throws Exception {
        Optional<Reservation> reservation = reservationRepository.findById(idReservation);
        if (reservation.isEmpty()) throw new Exception("Cette réservation n'existe pas !");
        reservation.get().setStatus("Terminée");
        reservation.get().getBook().setCurrentNumberReservation(reservation.get().getBook().getCurrentNumberReservation()-1);
        reservationRepository.saveAndFlush(reservation.get());
    }

    @Override
    public int checkCurrentPosition(int idReservation) throws Exception {
        Optional<Reservation> reservation = reservationRepository.findById(idReservation);
        if (reservation.isEmpty()) throw new Exception("Cette réservation n'existe pas !");
        List<Reservation> reservations = reservationRepository.findByBookAndStatus(reservation.get().getBook(),"en attente");
        int currentPosition = 1;
        boolean check = false;
        for (Reservation reservationFromList:reservations){
            if(check == false){
                if (!reservationFromList.getUserAccount().equals(reservation.get().getUserAccount())) currentPosition=currentPosition+1;
                else check=true;
            }
        }
        return currentPosition;
    }
}
