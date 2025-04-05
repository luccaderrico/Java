package com.services.wallet.resources.repositories.entities;

import com.services.wallet.domain.entities.Client;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import static com.services.wallet.domain.entities.enums.PersonType.toPersonType;
import static com.services.wallet.domain.entities.enums.Status.ACTIVE;

@Entity
@Table(name = "client")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class ClientJpa {

    @Id
    @Column(
            name = "document_number",
            length = 14,
            nullable = false
    )
    private String documentNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "person_type")
    private String personType;

    @Column(name = "status")
    private String status = ACTIVE.name();

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public ClientJpa(String documentNumber, String name, String personType) {
        this.documentNumber = documentNumber;
        this.name = name;
        this.personType = personType;
    }

    public static ClientJpa toClientJpa(Client client) {
        return new ClientJpa(
            client.documentNumber(),
            client.name(),
            client.personType().name()
        );
    }

    public static Client toClient(ClientJpa client) {
        return new Client(
            client.getDocumentNumber(),
            client.getName(),
            toPersonType(client.getPersonType())
        );
    }
}
