package com.springsecurity.rbac.springsecurityrbac.repository;

import com.springsecurity.rbac.springsecurityrbac.entity.security.PagesPrivileges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagesPrivilegesRepository extends JpaRepository<PagesPrivileges, Long> {


    @Query(
            value = "SELECT ppr from PagesPrivileges as ppr "
                    + "JOIN FETCH ppr.privilege pr "
                    + "JOIN FETCH ppr.page pg "
                    + "where pr.id = ?1 "
                    + "and pg.id = ?2 "
    )
    Optional<PagesPrivileges> alreadyExists(long privilegeId, long pageId);

    @Query(
            value = "SELECT ppr from PagesPrivileges as ppr "
                    + "JOIN FETCH ppr.privilege pr "
                    + "JOIN FETCH ppr.page pg "
                    + "where pr.name = ?1 "
                    + "and pg.name = ?2 "
    )
    boolean existByName(String privilegeName, String pageName);

    @Query(
            value = "SELECT ppr from PagesPrivileges as ppr "
                    + "JOIN FETCH ppr.privilege pr "
                    + "JOIN FETCH ppr.page pg "
                    + "where pr.name = ?1 "
                    + "and pg.name = ?2 "
    )
    PagesPrivileges findByName(String privilegeName, String pageName);
}
