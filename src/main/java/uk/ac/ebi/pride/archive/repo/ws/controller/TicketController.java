package uk.ac.ebi.pride.archive.repo.ws.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.pride.archive.repo.models.ticket.Ticket;
import uk.ac.ebi.pride.archive.repo.ws.service.TicketService;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/ticket")
@Slf4j
@Tag(name = "Ticket")
public class TicketController {

    private TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/findByTicketId/{ticketId}")
    public Ticket findByTicketId(@Valid @PathVariable String ticketId) {
        return ticketService.findById(ticketId);
    }

    @GetMapping("/findByAccession/{accession}")
    public Ticket findByAccession(@Valid @PathVariable String accession) {
        return ticketService.findByAccession(accession);
    }

    @PostMapping(path = "/save", consumes = "application/json")
    public Ticket save(@RequestBody Ticket ticket) {
        return ticketService.save(ticket);
    }
}
