package com.ocp7bibliotheque.bibliothequebatchmail.Service;

import com.ocp7bibliotheque.bibliothequebatchmail.DAO.BookRepository;
import com.ocp7bibliotheque.bibliothequebatchmail.DAO.LendingRepository;

import com.ocp7bibliotheque.bibliothequebatchmail.DAO.ReservationRepository;
import com.ocp7bibliotheque.bibliothequebatchmail.Entites.Lending;
import com.ocp7bibliotheque.bibliothequebatchmail.Entites.Reservation;
import com.ocp7bibliotheque.bibliothequebatchmail.Mail.MyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Configuration
@EnableScheduling
@Service
@Transactional
public class MailAlert {

    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    LendingRepository lendingRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ReservationRepository reservationRepository;

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

    @Scheduled(fixedRate = 86400000) // 24*60*60*1000
    public void mailDiffusorPret(){
        List<Lending> lendings = lendingRepository.findAll() ;
        for (Lending lending:lendings) {
            if(lending.getEndDate().isBefore(LocalDateTime.now())){
                // Create a Simple MailMessage.
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(lending.getUserAccount().getMail());
                message.setSubject("Prêt en retard");
                message.setText("Bonjour, votre prêt "+lending.getBook().getTitle()+" effectué le " +lending.getStartDate()+" à la librairie: "
                        +lending.getBook().getLibrary().getName() + " est arrivé à échéance. Si ce n'est pas déjà le cas vous pouvez le prolonger dans votre espace personnel. " +
                        "Nous vous prions de retourner l'ouvrage dans les plus brefs délais afin de régulariser votre situation.");
                // Send Message!
            this.emailSender.send(message);
            }
        }
    }

    @Scheduled(fixedRate = 3600000) // 60*60*1000
    public void mailDiffusorReservation() throws Exception {
        List<Reservation> reservations = reservationRepository.findByStatus("en cours") ; //utiliser status "en cours"
        for (Reservation reservation:reservations) {
            if(reservation.getEndDate().isBefore(LocalDateTime.now())){
                // Create a Simple MailMessage.
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(reservation.getUserAccount().getMail());
                message.setSubject("Réservation expirée");
                message.setText("Bonjour, votre réservation "+reservation.getBook().getTitle()+" effectué le " +reservation.getStartDate()+" à la librairie: "
                        +reservation.getLibrary().getName() + " est expirée. ");
                // Send Message!
                this.emailSender.send(message);
                reservation.setStatus("Expirée");
                reservation.getBook().setCurrentNumberReservation(reservation.getBook().getCurrentNumberReservation()-1);
                bookRepository.saveAndFlush(reservation.getBook());
                reservationRepository.saveAndFlush(reservation);
                //On envoie le mail pour le prochain qui a réservé cet ouvrage
                if (reservation.getBook().getCurrentNumberReservation()!=0){
                    Optional<Reservation> reservationNext = reservationRepository.findByBookAndStatusOrderById(reservation.getBook(),"en attente");
                    reservationNext.get().setStartDate(LocalDateTime.now());
                    reservationNext.get().setEndDate(reservationNext.get().getStartDate().plusDays(2));
                    SimpleMailMessage message2 = new SimpleMailMessage();
                    message2.setTo(reservationNext.get().getUserAccount().getMail());
                    message2.setSubject("Reservaton de l'ouvrage: "+reservationNext.get().getBook().getTitle());
                    message2.setText("Bonjour, l'ouvrage "+reservationNext.get().getBook().getTitle()+" a été réservé le " +reservationNext.get().getStartDate()+" à la librairie: "
                            +reservationNext.get().getLibrary().getName() + " ce dernier sera disponible pendant 48h.");
                    this.emailSender.send(message2);
                    reservationNext.get().setMailIsSend(true);
                    reservationNext.get().setStatus("en cours");
                    reservationRepository.saveAndFlush(reservationNext.get());
                }
            }
        }
    }
}
