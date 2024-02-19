package com.devsuperior.demo.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.demo.dto.EventDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventControllerIT {
	/*
		Critérios de correção:
		Mínimo para aprovação: 6 de 7

		- DELETE /cities/{id} deve retornar 404 Not Found quando id não existir

		- DELETE /cities/{id} deve retornar 204 No Content quando id for independente

		- DELETE /cities/{id} deve retornar 400 Bad Request quando id for dependente

		- POST /cities deve inserir recurso

		- GET /cities deve retornar recursos ordenados por nome

		- PUT /events deve atualizar recurso quando id existir

		- PUT /events deve retornar 404 Not Found quando id não existir

		Competências avaliadas:

		- Desenvolvimento TDD de API Rest com Java e Spring Boot

		- Implementação de cenários de busca, inserção, deleção e atualização

		- Tratamento de exceções em API com respostas HTTP customizadas
	 */

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void updateShouldUpdateResourceWhenIdExists() throws Exception {

		long existingId = 1L;

		EventDTO dto = new EventDTO(existingId, "Expo XP", LocalDate.of(2021, 5, 18), "https://expoxp.com.br", 7L);
		String jsonBody = objectMapper.writeValueAsString(dto);
		
		ResultActions result =
				mockMvc.perform(put("/events/{id}", existingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.id").value(1L));
		result.andExpect(jsonPath("$.name").value("Expo XP"));
		result.andExpect(jsonPath("$.date").value("2021-05-18"));
		result.andExpect(jsonPath("$.url").value("https://expoxp.com.br"));
		result.andExpect(jsonPath("$.cityId").value(7L));
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
		long nonExistingId = 1000L;

		EventDTO dto = new EventDTO(null, "Expo XP", LocalDate.of(2021, 5, 18), "https://expoxp.com.br", 7L);
		String jsonBody = objectMapper.writeValueAsString(dto);
		
		ResultActions result =
				mockMvc.perform(put("/events/{id}", nonExistingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
}