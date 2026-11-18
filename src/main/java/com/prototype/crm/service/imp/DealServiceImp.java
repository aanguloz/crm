package com.prototype.crm.service.imp;

import com.prototype.crm.model.dto.emuns.DealStage;
import com.prototype.crm.model.dto.request.DealReqDTO;
import com.prototype.crm.model.dto.response.DealRespDTO;
import com.prototype.crm.model.entity.Deal;
import com.prototype.crm.repository.DealRepository;
import com.prototype.crm.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealServiceImp implements DealService {

    private final DealRepository dealRepository;

    @Override
    @Transactional
    public DealRespDTO createDeal(DealReqDTO dealReqDTO) {

        Deal deal = new Deal();
        deal.setTitle(dealReqDTO.getTitle());
        deal.setCompany(dealReqDTO.getCompany());
        deal.setAmount(dealReqDTO.getAmount());
        deal.setStage(dealReqDTO.getStage());
        // deal.setClient(clientsRepository.findById(dto.getClient().getId()).orElseThrow());

        return mapToRespDTO(dealRepository.save(deal));

    }

    @Override
    @Transactional
    public DealRespDTO updateDeal(UUID id, DealReqDTO dealReqDTO) {

        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada con ID: " + id));

        deal.setTitle(dealReqDTO.getTitle());
        deal.setCompany(dealReqDTO.getCompany());
        deal.setAmount(dealReqDTO.getAmount());
        deal.setStage(dealReqDTO.getStage());

        return mapToRespDTO(dealRepository.save(deal));

    }

    @Override
    public DealRespDTO moveDealStage(UUID id, DealStage newStage) {

        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada con ID: " + id));
        deal.setStage(newStage);
        return mapToRespDTO(dealRepository.save(deal));

    }

    @Override
    public void deleteDeal(UUID id) {

        if (!dealRepository.existsById(id)) {
            throw new RuntimeException("Oportunidad no encontrada con ID: " + id);
        }
        dealRepository.deleteById(id);

    }

    @Override
    public List<DealRespDTO> searchDeal(String filter) {

        List<Deal> deals;

        // Si el filtro está vacío o es nulo, traemos todos los deals
        if (filter == null || filter.trim().isEmpty()) {
            deals = dealRepository.findAll();
        } else {
            // Si hay filtro, usamos nuestro nuevo método @Query
            deals = dealRepository.searchByTitleOrCompany(filter.trim());
        }

        return deals.stream().map(this::mapToRespDTO).collect(Collectors.toList());

    }

    private DealRespDTO mapToRespDTO(Deal deal) {
        DealRespDTO dto = new DealRespDTO();
        dto.setId(deal.getId());
        dto.setTitle(deal.getTitle());
        dto.setCompany(deal.getCompany());
        dto.setAmount(deal.getAmount());
        dto.setStage(deal.getStage());
        return dto;
    }

}
