package pl.akademiaspring.carsapi.service;

import org.springframework.stereotype.Service;
import pl.akademiaspring.carsapi.controller.CarsApi;
import pl.akademiaspring.carsapi.model.Car;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;

@Service
public class CarService {

    private List<Car> carList;

    public CarService() {
        this.carList = new ArrayList<>();
        carList.add(new Car(1L,"Fiat","Punto","silver"));
        carList.add(new Car(2L,"Volksvagen","Golf","black"));
        carList.add(new Car(3L,"Ford","Focus","silver"));
    }

    public List<Car> getAllCars() {
        carList.forEach(car -> car.addIf(!car.hasLinks(), () -> linkTo(CarsApi.class).slash(car.getId()).withSelfRel()));
        Collections.sort(carList, Comparator.comparingLong(Car::getId));
        return carList;
    }

    public Optional<Car> getCarById(long id) {
        return carList.stream().filter(car -> car.getId() == id).findFirst();
    }

    public List<Car> getCarByColor(String color) {
        List<Car> cars = carList.stream().filter(car -> color.equalsIgnoreCase(car.getColor())).collect(Collectors.toList());
        return cars;
    }

    public boolean addCar(Car car) {
        return carList.add(car);
    }

    public boolean modCar(Car newCar) {
        Optional<Car> first = carList.stream().filter(car -> car.getId() == newCar.getId()).findFirst();
        if(first.isPresent()) {
            carList.remove(first.get());
            return carList.add(newCar);
        }
        return false;
    }

    public boolean deleteCar(long id) {
        Optional<Car> findCar = getCarById(id);
        if(findCar.isPresent()) {
            return carList.remove(findCar.get());
        }
        return false;
    }
}
