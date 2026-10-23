package com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.entity.Setting;

public interface SettingRepository extends JpaRepository<Setting, String> {}