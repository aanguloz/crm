package com.prototype.crm.repository;

import com.prototype.crm.model.entity.Module;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    @Query(value = """
        select m.code, m.name, m.path, a.id, a.code, a.action, a.description
        from module m
        left join action a on a.id = m.action.id
        where 
    """, nativeQuery = true)
    List<Object[]> searchModule(@Param("filter") String filter);

}
