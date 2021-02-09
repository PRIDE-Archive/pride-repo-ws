package uk.ac.ebi.pride.archive.repo.ws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import uk.ac.ebi.pride.archive.repo.models.ticket.Ticket;
import uk.ac.ebi.pride.archive.repo.ws.repository.TicketRepository;

@Service
@Transactional(readOnly = true)
@Slf4j
public class TicketService {

    private TicketRepository ticketRepository;

    @Autowired
    public TicketService(
            TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket findById(String ticketId) {
        Assert.notNull(ticketId, "TicketId cannot be empty");
        return ticketRepository.findByTicketId(ticketId);
    }

    public Ticket findByAccession(String accession) {
        Assert.notNull(accession, "accession cannot be empty");
        return ticketRepository.findByAccession(accession);
    }

    @Transactional(readOnly = false)
    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
}
