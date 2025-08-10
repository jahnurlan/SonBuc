package com.example.userms;

import com.example.userms.model.entity.Role;
import com.example.userms.model.enums.RoleType;
import com.example.userms.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class UserMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserMsApplication.class, args);
	}

	@Component
	@RequiredArgsConstructor
	public static class DataLoader implements CommandLineRunner {
		private final RoleRepository roleRepository;

		@Override
		public void run(String... args) {
			RoleType userRoleName = RoleType.USER;
			RoleType guestRoleName = RoleType.GUEST;
			RoleType adminRoleName = RoleType.ADMIN;

			roleRepository.findRoleByName(userRoleName)
					.ifPresentOrElse(
							role -> System.out.println("Role already exists: " + userRoleName),
							() -> {
								Role newRole = Role.builder()
										.name(userRoleName)
										.build();
								roleRepository.save(newRole);
								System.out.println("New role created: " + userRoleName);
							}
					);
			roleRepository.findRoleByName(guestRoleName)
					.ifPresentOrElse(
							role -> System.out.println("Role already exists: " + guestRoleName),
							() -> {
								Role newRole = Role.builder()
										.name(guestRoleName)
										.build();
								roleRepository.save(newRole);
								System.out.println("New role created: " + guestRoleName);
							}
					);
			roleRepository.findRoleByName(adminRoleName)
					.ifPresentOrElse(
							role -> System.out.println("Role already exists: " + adminRoleName),
							() -> {
								Role newRole = Role.builder()
										.name(adminRoleName)
										.build();
								roleRepository.save(newRole);
								System.out.println("New role created: " + adminRoleName);
							}
					);
		}
	}
}
