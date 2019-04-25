package com.matiasnicoletti.apiservicecatalog.repository;


import com.matiasnicoletti.apiservicecatalog.model.Api;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApiRepository extends CrudRepository<Api, String> {

  @Query(nativeQuery = true,
      value = "select t1.name,t1.swagger_url,t1.port,t1.version,t1.date_updated, swagger "
          + "from apis t1 order by t1.name")
  List<Api> findByPaths();

  @Query(
      value = "select new Api(a.name,a.swaggerUrl,a.port,a.version,a.dateUpdated) "
          + "from Api a order by a.name")
  List<Api> findAllApis();

  @Query(value =
      "select new Api(a.name,a.swaggerUrl,a.port,a.version,a.dateUpdated)"
          + " from Api a WHERE a.name = ?1")
  Api findOneByName(String serviceName);

  @Query(nativeQuery = true,
      value = "select t1.name,t1.swagger_url,t1.port,t1.version,t1.date_updated,swagger->'paths' swagger "
          + "from apis t1 where t1.name=?1")
  Api findPathsById(String name);


  @Transactional
  void deleteApiByNameNotIn(List<String> names);

  @Transactional
  void deleteByName(String name);
}
