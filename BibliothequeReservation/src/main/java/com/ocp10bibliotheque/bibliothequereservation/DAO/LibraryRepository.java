package com.ocp10bibliotheque.bibliothequereservation.DAO;


import com.ocp10bibliotheque.bibliothequereservation.Entites.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibraryRepository extends JpaRepository<Library,Integer>{

    Optional<Library> findById(int idLibrary) throws Exception;

}
