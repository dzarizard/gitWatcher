package com.dzaro.gitWatcher.repositories;

import com.dzaro.gitWatcher.dto.ReleaseData;
import org.springframework.data.repository.CrudRepository;

public interface ReleaseRepository extends CrudRepository<ReleaseData, Long> {

	ReleaseData findByUserNameAndProjectName(String userName, String projectName);
}