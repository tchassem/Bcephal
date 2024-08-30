/**
 * 
 */
package com.moriset.bcephal.initiation.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.moriset.bcephal.initiation.domain.Attribute;

/**
 * @author Joseph Wambo
 *
 */
public interface IUniverseGenerator extends JpaRepository<Attribute, Long> {

}
