package com.devsuperior.demo.services;

import com.devsuperior.demo.dto.CityDTO;
import com.devsuperior.demo.entities.City;
import com.devsuperior.demo.exceptions.BadRequestException;
import com.devsuperior.demo.exceptions.DatabaseException;
import com.devsuperior.demo.exceptions.ResourceNotFoundException;
import com.devsuperior.demo.repositories.CityRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityService {

    private CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional(readOnly = true)
    public List<CityDTO> findAll() {
        List<City> list = cityRepository.findAll(Sort.by("name"));
        return list.stream().map(x -> new CityDTO(x)).collect(Collectors.toList());
    }

    @Transactional
    public CityDTO insert(CityDTO dto) {
        City city = new City();
        city.setName(dto.getName());
        city = cityRepository.save(city);
        return new CityDTO(city);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        City city = cityRepository.getReferenceById(id);
        if (!cityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found!");
        }
        try {
            cityRepository.deleteById(id);
        }catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure!");
        }
        if (!city.getEvents().isEmpty()) {
            throw new BadRequestException("The city has one or more event(s)");
        }
    }
}