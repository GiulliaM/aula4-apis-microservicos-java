package br.ifsp.contacts.dto;

import br.ifsp.contacts.model.Address;

public record AddressResponseDTO(
        Long id,
        String rua,
        String cidade,
        String estado,
        String cep
) {
    public AddressResponseDTO(Address address) {
        this(address.getId(), address.getRua(), address.getCidade(), address.getEstado(), address.getCep());
    }
}