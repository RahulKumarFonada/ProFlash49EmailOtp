package com.flash49.emailOtp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flash49.emailOtp.entity.CleverTapEntity;

@Repository
public interface CleverTapRepository extends JpaRepository<CleverTapEntity, Long> {

	@Query(value = "select * from clever_tap where sent_dlr=0", nativeQuery = true)
	List<CleverTapEntity> findBySentDrl();

	@Modifying
	@Transactional
	@Query(value = "update clever_tap set sent_dlr=:dlrStatus where id=:id", nativeQuery = true)
	void updateById(@Param("dlrStatus") String dlrStatus, @Param("id") Long id);
}
