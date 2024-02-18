package com.devsuperior.demo.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.demo.dto.CityDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CityControllerIT {
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

	private Long dependentId;
	private Long nonExistingId;
	private Long independentId;

	@BeforeEach
	void setUp() throws Exception {
		dependentId = 1L;
		nonExistingId = 50L;
		independentId = 5L;
	}
	
	@Test
	public void findAllShouldReturnAllResourcesSortedByName() throws Exception {
		ResultActions result =
				mockMvc.perform(get("/cities")
					.contentType(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$[0].name").value("Belo Horizonte"));
		result.andExpect(jsonPath("$[1].name").value("Belém"));
		result.andExpect(jsonPath("$[2].name").value("Brasília"));
	}
	
	@Test
	public void insertShouldInsertResource() throws Exception {
		CityDTO dto = new CityDTO(null, "Recife");
		String jsonBody = objectMapper.writeValueAsString(dto);
		
		ResultActions result =
				mockMvc.perform(post("/cities")
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").value("Recife"));
	}

	@Test
	public void deleteShouldReturnNoContentWhenIndependentId() throws Exception {
		ResultActions result =
				mockMvc.perform(delete("/cities/{id}", independentId));

		result.andExpect(status().isNoContent());
	}

	@Test
	public void deleteShouldReturnNotFoundWhenNonExistingId() throws Exception {
		ResultActions result =
				mockMvc.perform(delete("/cities/{id}", nonExistingId));

		result.andExpect(status().isNotFound());
	}

	@Test
	public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
		ResultActions result =
				mockMvc.perform(delete("/cities/{id}", dependentId));
				
		result.andExpect(status().isBadRequest());
	}
}