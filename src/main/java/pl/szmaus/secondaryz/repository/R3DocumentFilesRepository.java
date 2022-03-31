package pl.szmaus.secondaryz.repository;

import org.springframework.data.repository.CrudRepository;

import pl.szmaus.secondaryz.entity.R3DocumentFiles;


public interface R3DocumentFilesRepository extends CrudRepository<R3DocumentFiles,String> {
   R3DocumentFiles findByGuid(String s);

}
