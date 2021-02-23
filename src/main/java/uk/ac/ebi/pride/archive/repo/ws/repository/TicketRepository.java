package uk.ac.ebi.pride.archive.repo.ws.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.pride.archive.repo.models.ticket.Ticket;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {

    Ticket findByTicketId(String ticketId);

    Ticket findByAccession(String accession);

    List<Ticket> findAllByState(Ticket.State state);
}
