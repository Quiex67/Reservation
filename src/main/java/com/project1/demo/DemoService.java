package com.project1.demo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DemoService {
    private static final Logger log = LoggerFactory.getLogger(DemoService.class);

    private final ReservationRepository Repository;
    public DemoService(ReservationRepository repository){
        Repository = repository;
    }

    public Reservation getDemoByID(Long id){
        ReservationEntity reservationEntity=
                Repository.findById(id).orElseThrow(()->new EntityNotFoundException("Not Found id:"+id));
        return toDomainReservation(reservationEntity);
    }

    public List<Reservation> getAllID() {
        List<ReservationEntity> allID=Repository.findAll();
        return allID.stream().map(this::toDomainReservation).toList();

    }

    public Reservation createDemo(Reservation reservationtocreate) {

    if(reservationtocreate.status()!=null){
        throw new IllegalArgumentException("Status should be empty");
    }
    if(!reservationtocreate.endDate().isAfter(reservationtocreate.startDate())){
        throw new IllegalArgumentException("End date should be after start date");
    }
    var EntityToSave=new ReservationEntity(
            null,
            reservationtocreate.userId(),
            reservationtocreate.RoomId(),
            reservationtocreate.startDate(),
            reservationtocreate.endDate(),
            ReservationStatus.PENDING
    );
    var savedEntity=Repository.save(EntityToSave);
    return toDomainReservation(savedEntity);
    }

    public Reservation updateDemo(Long id, Reservation reservationtoupdate) {
        var reservationEntity=Repository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("Not Found id:"+id));
        if(!reservationtoupdate.endDate().isAfter(reservationtoupdate.startDate())){
            throw new IllegalArgumentException("End date should be after start date");
        }

        if(reservationEntity.getStatus()!=ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Status should be PENDING");
        }var ReservationToUpdate=new ReservationEntity(
                reservationEntity.getId(),
                reservationtoupdate.userId(),
                reservationtoupdate.RoomId(),
                reservationtoupdate.startDate(),
                reservationtoupdate.endDate(),
                ReservationStatus.PENDING
        );
        var updatedReservation= Repository.save(ReservationToUpdate);
        return toDomainReservation(updatedReservation);
    }
    @Transactional
    public void cancelReservation(Long id) {

        var reservationEntity=Repository.findById(id).orElseThrow(()->new EntityNotFoundException("Not Found id:"+id));
        if(reservationEntity.getStatus()!=ReservationStatus.PENDING) {
            throw new IllegalArgumentException("That would cancel reservation status reservation should be PENDING");
        }
        Repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Reservation has been cancelled id: "+id);
    }


    public Reservation approveReservation(Long id) {
        var reservationEntity=Repository.findById(id).orElseThrow(
                ()->new EntityNotFoundException("Not Found id:"+id));
        if(reservationEntity.getStatus()!=ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Cannot approve reservation with status " + reservationEntity.getStatus());
        }
        var isConflict=isReservationConflicted(reservationEntity);
        if(isConflict) {
            throw new IllegalArgumentException("Can't approve reservation because reservation is conflicted");
        }
        reservationEntity.setStatus(ReservationStatus.APPROVED);
        Repository.save(reservationEntity);
        return toDomainReservation(reservationEntity);
    }
    private boolean isReservationConflicted(ReservationEntity reservation) {
        var allReservarions=Repository.findAll();
        for(ReservationEntity exitingReservation:allReservarions) {
            if(reservation.getId().equals(exitingReservation.getId())) {
                continue;
            }if(!reservation.getRoomId().equals(exitingReservation.getRoomId())) {
                continue;
            }
            if(!exitingReservation.getStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }if(reservation.getStartDate().isBefore(exitingReservation.getEndDate())
            &&exitingReservation.getStartDate().isBefore(reservation.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    private Reservation toDomainReservation(ReservationEntity reservationentity) {
     return new Reservation(
             reservationentity.getId(),
             reservationentity.getUserId(),
             reservationentity.getRoomId(),
             reservationentity.getStartDate(),
             reservationentity.getEndDate(),
             reservationentity.getStatus()
     );
    }
}
