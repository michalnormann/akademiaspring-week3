package pl.akademiaspring.carsapi.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.akademiaspring.carsapi.model.Car;
import pl.akademiaspring.carsapi.service.CarService;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/cars", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class CarsApi {

    private CarService carService;

    @Autowired
    public CarsApi(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<Car>> getCars() {
        Link link = linkTo(CarsApi.class).withSelfRel();
        CollectionModel<Car> carCollectionModel = new CollectionModel<>(carService.getAllCars(), link);
        return new ResponseEntity<>(carCollectionModel, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Car>> getCar(@PathVariable long id) {
        Link link = linkTo(CarsApi.class).slash(id).withSelfRel();
        Optional<Car> car = carService.getCarById(id);
        if(!car.equals(Optional.empty())) {
            EntityModel<Car> carEntityModel = new EntityModel(car.get(), link);
            return new ResponseEntity<>(carEntityModel, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<CollectionModel<Car>> getCarByColor(@PathVariable String color) {
        List<Car> cars = carService.getCarByColor(color);
        if (cars.size() > 0) {
            cars.forEach(car -> car.addIf(!car.hasLinks(), () -> linkTo(CarsApi.class).slash(car.getId()).withSelfRel()));
            Link link = linkTo(CarsApi.class).withSelfRel();
            CollectionModel<Car> carCollectionModel = new CollectionModel<>(cars, link);
            return new ResponseEntity<>(carCollectionModel, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity addCar(@RequestBody Car car) {
        return new ResponseEntity<>(carService.addCar(car),HttpStatus.OK) ;
    }

    @PutMapping
    public ResponseEntity<Boolean> editCar(@RequestBody Car newCar) {
        if(carService.modCar(newCar)) {
            carService.modCar(newCar);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity removeCar(@PathVariable long id) {
        if(carService.deleteCar(id)) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}
