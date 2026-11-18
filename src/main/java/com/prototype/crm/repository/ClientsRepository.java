package com.prototype.crm.repository;

import com.prototype.crm.model.dto.emuns.ClientsStatus;
import com.prototype.crm.model.dto.response.ClientsRespDTO;
import com.prototype.crm.model.entity.Clients;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ClientsRepository extends JpaRepository<Clients, UUID> {

    boolean existsByFullName(String fullName);

    boolean existsByIdentifier(String identifier);

    @Query(value = """
    select 
        cast(c.id as char), c.identifier, c.full_name, c.company, c.email, c.phone, c.sector, cast(c.status as char), c.estimated_value,
        cast(c.owner_id as char), c.created_at
    from clients c
    where (
        (:filter = '' or :filter is null
        or lower(c.full_name) like lower(concat('%', :filter, '%'))
        or lower(c.company) like lower(concat('%', :filter, '%')))
        and c.is_active = true
    )
    """, nativeQuery = true)
    List<ClientsRespDTO> searchClients(@Param("filter") String filter);

    long countByStatusIn(List<ClientsStatus> statuses);

    long countByStatus(ClientsStatus status);

}
