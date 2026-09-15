package com.project1.demo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DemoContoller {
    private static final Logger logger = LoggerFactory.getLogger(DemoContoller.class);
    private DemoService demoService;
    public DemoContoller(DemoService demoService) {
        this.demoService = demoService;
    }
    @GetMapping("/{id}")
    public ResponseEntity< Reservation> getDemoByID(@PathVariable("id") Long id){
        logger.info("Called getDemoByID id: "+id);
            return ResponseEntity.ok(demoService.getDemoByID(id));

    }
    @GetMapping()
    public ResponseEntity< List<Reservation>> getAllDemo(){
        logger.info("Called getAllDemo");
        return ResponseEntity.ok(demoService.getAllID());
    }
    @PostMapping()
    public ResponseEntity <Reservation> createDemo(@Valid @RequestBody  Reservation reservationtocreate){
        logger.info("Called createDemo");
        return ResponseEntity.status(HttpStatus.CREATED).
                body(demoService.createDemo(reservationtocreate));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateDemo(@PathVariable("id") Long id,
                                                  @Valid @RequestBody  Reservation reservationtoupdate){
        logger.info("Called updateDemo id: "+id);

        var updated= demoService.updateDemo(id,reservationtoupdate);
        return ResponseEntity.ok(updated);
    }
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> deleteDemo(@PathVariable("id") Long id){
        logger.info("Called deleteDemo id: "+id);
            demoService.cancelReservation(id);
            return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Reservation> approveDemo(@PathVariable("id") Long id){
        logger.info("Approving Demo with id {}",id);
        var reservation= demoService.approveReservation(id);
        return ResponseEntity.ok(reservation);
    }
}
