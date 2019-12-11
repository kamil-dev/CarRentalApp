package com.mkcomp.CarRentalApp.service.impl;

import com.mkcomp.CarRentalApp.api.request.AddCarRequest;
import com.mkcomp.CarRentalApp.model.Branch;
import com.mkcomp.CarRentalApp.model.Car;
import com.mkcomp.CarRentalApp.model.Reservation;
import com.mkcomp.CarRentalApp.repository.BranchRepository;
import com.mkcomp.CarRentalApp.repository.CarRepository;
import com.mkcomp.CarRentalApp.service.CarService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {
    private CarRepository carRepository;
    private BranchRepository branchRepository;

    public CarServiceImpl(CarRepository carRepository, BranchRepository branchRepository) {
        this.carRepository = carRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    public Long addCar(AddCarRequest request) {
        Branch branch = extractBranchFromRepository(request.getBranchId());
        Car car = addCarToDataSource(request, branch);
        return car.getId();
    }

    @Override
    public Car findCarById(Long id) {
        return carRepository.getOne(id);
    }

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public void deleteCarById(long id) {
        carRepository.deleteById(id);
    }

    private Branch extractBranchFromRepository(Long branchId) {
        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (!optionalBranch.isPresent()) {
            throw new IllegalArgumentException("There is no such a branch within database");
        }
        return optionalBranch.get();
    }

    private Car addCarToDataSource(AddCarRequest request, Branch branch){
        Car car = new Car();
        car.setBasePricePerDay(request.getBasePricePerDay());
        car.setBrand(request.getBrand());
        car.setBranch(branch);
        car.setModel(request.getModel());
        car.setProductionYear(request.getProductionYear());
        car.setSpecification(request.getSpecification());
        return carRepository.save(car);
    }

    @Override
    public List<Car> findAvailableCars(LocalDateTime startDate, LocalDateTime endDate, long branchId) {
        Branch branch = branchRepository.getOne(branchId);

        List<Car> carsFromTheBranch = carRepository.findAllByBranchIs(branch);
        List<Car> availableCars = new ArrayList<>();

        LocalDateTime reservationEnd;
        LocalDateTime reservationStart;
        for (Car c : carsFromTheBranch) {
            List<Reservation> reservations = c.getReservations();
            boolean isAvailable = true;
            if (reservations != null) {
                for (Reservation r : reservations){
                    reservationEnd = r.getReservationEnd();
                    reservationStart = r.getReservationStart();
                    if (!((startDate.isBefore(reservationStart) && endDate.isBefore(reservationStart)) ||
                    startDate.isAfter(reservationEnd) && endDate.isAfter(reservationEnd))) isAvailable = false;
                }
            }
            if (isAvailable) availableCars.add(c);
        }
        return availableCars;
    }

    public void updateCar(Car car){

    }

    @Override
    public void updateCar(AddCarRequest request, long id) {
        Branch branch = branchRepository.getOne(request.getBranchId());
        Car car = new Car();
        car.setBasePricePerDay(request.getBasePricePerDay());
        car.setBrand(request.getBrand());
        car.setBranch(branch);
        car.setModel(request.getModel());
        car.setProductionYear(request.getProductionYear());
        car.setSpecification(request.getSpecification());
        car.setId(id);
        Car previousCopy = carRepository.getOne(id);
        previousCopy = car;
        carRepository.save(previousCopy);
    }
}
