package cn.edu.zju.cs.jobmate.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * User entity.
 */
@Entity
@Table(name = "user")
public class User implements UserDetails {

}
