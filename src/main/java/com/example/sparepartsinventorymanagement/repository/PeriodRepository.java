package com.example.sparepartsinventorymanagement.repository;

import com.example.sparepartsinventorymanagement.entities.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {
    Period findByStartDateAndEndDate(Date sDate, Date eDate);
}
