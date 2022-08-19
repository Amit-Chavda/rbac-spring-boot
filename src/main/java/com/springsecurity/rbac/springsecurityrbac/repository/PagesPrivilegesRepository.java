package com.springsecurity.rbac.springsecurityrbac.repository;

import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PagesPrivilegesRepository extends JpaRepository<PagesPrivileges, Long> {


    @Query(
            value = "SELECT case when (count(ppr)>0) then true else false end from PagesPrivileges as ppr "
                    + "JOIN ppr.privilege pr "
                    + "JOIN ppr.page pg "
                    + "where pr.name = ?1 "
                    + "and pg.name = ?2 "
    )
    boolean existsByName(String privilegeName, String pageName);

    @Query(
            value = "SELECT ppr from PagesPrivileges as ppr "
                    + "JOIN FETCH ppr.privilege pr "
                    + "JOIN FETCH ppr.page pg "
                    + "where pr.name = ?1 "
                    + "and pg.name = ?2 "
    )
    PagesPrivileges findByName(String privilegeName, String pageName);
}
