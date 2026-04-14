package br.ifsp.contacts.repository;

import br.ifsp.contacts.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByContact_Id(Long contactId);
}
