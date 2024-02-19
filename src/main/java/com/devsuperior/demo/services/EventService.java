package com.devsuperior.demo.services;

import com.devsuperior.demo.dto.EventDTO;
import com.devsuperior.demo.entities.City;
import com.devsuperior.demo.entities.Event;
import com.devsuperior.demo.exceptions.ResourceNotFoundException;
import com.devsuperior.demo.repositories.CityRepository;
import com.devsuperior.demo.repositories.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private EventRepository eventRepository;

    private CityRepository cityRepository;

    public EventService(EventRepository eventRepository, CityRepository cityRepository) {
        this.eventRepository = eventRepository;
        this.cityRepository = cityRepository;
    }

    private void copyDtoToEntity(EventDTO eventDTO, Event entity) {
        entity.setId(eventDTO.getId());
        entity.setName(eventDTO.getName());
        entity.setDate(eventDTO.getDate());
        entity.setUrl(eventDTO.getUrl());
        City city = cityRepository.getReferenceById(eventDTO.getCityId());
        entity.setCity(city);
    }

    @Transactional
    public EventDTO update(Long id, EventDTO dto) {
        try {
            Event entity = eventRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = eventRepository.save(entity);
            return new EventDTO(entity);
        }catch (EntityNotFoundException e) {
            throw new  ResourceNotFoundException("Resource not found!");
        }
    }
}