package com.prototype.crm.service.imp;

import com.prototype.crm.model.dto.request.ClientsReqDTO;
import com.prototype.crm.model.dto.response.ClientsRespDTO;
import com.prototype.crm.model.entity.Clients;
import com.prototype.crm.model.entity.Users;
import com.prototype.crm.repository.ClientsRepository;
import com.prototype.crm.repository.UserRepository;
import com.prototype.crm.service.ClientsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientsServiceImp implements ClientsService {

    private final ClientsRepository clientsRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ClientsRespDTO addClients(ClientsReqDTO clientsReqDTO) {

        if (clientsRepository.existsByFullName(clientsReqDTO.getFullName()) ||
                clientsRepository.existsByIdentifier(clientsReqDTO.getIdentifier())) {
            throw new IllegalArgumentException("El cliente o identificador ya está registrado.");
        }

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName().trim();
        log.info("🔍 Intentando buscar en la BD el username: '[{}]'", currentUsername);
        Users owner = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado en la base de datos."));

        Clients client = Clients.builder()
                .identifier(clientsReqDTO.getIdentifier() != null ? clientsReqDTO.getIdentifier() : "CLI-" + System.currentTimeMillis())
                .fullName(clientsReqDTO.getFullName())
                .company(clientsReqDTO.getCompany())
                .email(clientsReqDTO.getEmail())
                .phone(clientsReqDTO.getPhone())
                .sector(clientsReqDTO.getSector())
                .status(clientsReqDTO.getStatus())
                .estimatedValue(clientsReqDTO.getEstimatedValue())
                .isActive(true)
                .owner(owner)
                .build();

        Clients savedClient = clientsRepository.save(client);

        return new ClientsRespDTO(
                savedClient.getId().toString(),
                savedClient.getIdentifier(),
                savedClient.getFullName(),
                savedClient.getCompany(),
                savedClient.getEmail(),
                savedClient.getPhone(),
                savedClient.getSector(),
                savedClient.getStatus().toString(),
                savedClient.getEstimatedValue(),
                savedClient.getOwner().getId().toString(),
                savedClient.getCreatedAt()
        );
    }

    @Override
    public List<ClientsRespDTO> searchClients(String filter) {
        List<ClientsRespDTO> clientsList = clientsRepository.searchClients(filter);
        return clientsList;
    }

    @Override
    public ClientsRespDTO getClientById(UUID id) {
        Clients client = clientsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
        return mapToDTO(client);
    }

    @Override
    @Transactional
    public ClientsRespDTO updateClient(UUID id, ClientsReqDTO clientsReqDTO) {
        Clients client = clientsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        client.setFullName(clientsReqDTO.getFullName());
        client.setCompany(clientsReqDTO.getCompany());
        client.setEmail(clientsReqDTO.getEmail());
        client.setPhone(clientsReqDTO.getPhone());
        client.setSector(clientsReqDTO.getSector());
        client.setStatus(clientsReqDTO.getStatus());
        client.setEstimatedValue(clientsReqDTO.getEstimatedValue());

        Clients updatedClient = clientsRepository.save(client);
        return mapToDTO(updatedClient);
    }

    @Override
    @Transactional
    public void deactivateClient(UUID id) {
        Clients client = clientsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));

        client.setIsActive(false); // Eliminación lógica
        clientsRepository.save(client);
    }

    private ClientsRespDTO mapToDTO(Clients client) {
        return new ClientsRespDTO(
                client.getId().toString(),
                client.getIdentifier(),
                client.getFullName(),
                client.getCompany(),
                client.getEmail(),
                client.getPhone(),
                client.getSector(),
                client.getStatus().toString(),
                client.getEstimatedValue(),
                client.getOwner().getId().toString(),
                client.getCreatedAt()
        );
    }
}
