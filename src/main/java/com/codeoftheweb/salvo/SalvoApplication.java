package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			Date d1 = new Date();
			Player p1 = playerRepository.save(new Player("Jack", "24"));
			Player p2 = playerRepository.save(new Player("Chloe", "42"));
			Player p3 = playerRepository.save(new Player("Kim", "kb"));
			Player p4 = playerRepository.save(new Player("Tony", "mole"));

			Game g1 = gameRepository.save(new Game());
			Game g2 = gameRepository.save(new Game(Date.from(d1.toInstant().plusSeconds(3600))));
			Game g3 = gameRepository.save(new Game(Date.from(d1.toInstant().plusSeconds(7200))));
			Game g4 = gameRepository.save(new Game());
			Game g5 = gameRepository.save(new Game());
			Game g6 = gameRepository.save(new Game());
			Game g7 = gameRepository.save(new Game());
			Game g8 = gameRepository.save(new Game());

			GamePlayer gp1 = gamePlayerRepository.save(new GamePlayer(g1,p1));
			GamePlayer gp2 = gamePlayerRepository.save(new GamePlayer(g1,p2));
			GamePlayer gp3 = gamePlayerRepository.save(new GamePlayer(g2,p1));
			GamePlayer gp4 = gamePlayerRepository.save(new GamePlayer(g2,p2));
			GamePlayer gp5 = gamePlayerRepository.save(new GamePlayer(g3,p2));
			GamePlayer gp6 = gamePlayerRepository.save(new GamePlayer(g3,p4));
			GamePlayer gp7 = gamePlayerRepository.save(new GamePlayer(g4,p2));
			GamePlayer gp8 = gamePlayerRepository.save(new GamePlayer(g4,p1));
			GamePlayer gp9 = gamePlayerRepository.save(new GamePlayer(g5,p4));
			GamePlayer gp10 = gamePlayerRepository.save(new GamePlayer(g5,p1));
			GamePlayer gp11 = gamePlayerRepository.save(new GamePlayer(g6,p3));
			GamePlayer gp13 = gamePlayerRepository.save(new GamePlayer(g7,p4));
			GamePlayer gp15 = gamePlayerRepository.save(new GamePlayer(g8,p3));
			GamePlayer gp16 = gamePlayerRepository.save(new GamePlayer(g8,p4));

			/////SHIPS////
			Ship s1 = shipRepository.save(new Ship(gp1,"Destroyer", Arrays.asList("H2", "H3", "H4")));
			Ship s2 = shipRepository.save(new Ship(gp1,"Submarine", Arrays.asList("E1", "F1", "G1")));
			Ship s3 = shipRepository.save(new Ship(gp1,"Patrol Boat", Arrays.asList("B4", "B5")));

			Ship s4 = shipRepository.save(new Ship(gp2,"Destroyer", Arrays.asList("B5", "C5", "D5")));
			Ship s5 = shipRepository.save(new Ship(gp2,"Patrol Boat", Arrays.asList("F1", "F2")));

			Ship s6 = shipRepository.save(new Ship(gp3,"Destroyer", Arrays.asList("B5", "C5", "D5")));
			Ship s7 = shipRepository.save(new Ship(gp3,"Patrol Boat", Arrays.asList("C6", "C7")));

			Ship s8 = shipRepository.save(new Ship(gp4,"Submarine", Arrays.asList("A2", "A3", "A4")));
			Ship s9 = shipRepository.save(new Ship(gp4,"Patrol Boat", Arrays.asList("G6", "H6")));

			Ship s10 = shipRepository.save(new Ship(gp5,"Destroyer", Arrays.asList("B5", "C5", "D5")));
			Ship s11 = shipRepository.save(new Ship(gp5,"Patrol Boat", Arrays.asList("C6", "C7")));

			Ship s12 = shipRepository.save(new Ship(gp6,"Submarine", Arrays.asList("A2", "A3", "A4")));
			Ship s13 = shipRepository.save(new Ship(gp6,"Patrol Boat", Arrays.asList("G6", "H6")));

			Ship s14 = shipRepository.save(new Ship(gp7,"Destroyer", Arrays.asList("B5", "C5", "D5")));
			Ship s15 = shipRepository.save(new Ship(gp7,"Patrol Boat", Arrays.asList("C6", "C7")));

			Ship s16 = shipRepository.save(new Ship(gp8,"Submarine", Arrays.asList("A2", "A3", "A4")));
			Ship s17 = shipRepository.save(new Ship(gp8,"Patrol Boat", Arrays.asList("G6", "H6")));

			Ship s18 = shipRepository.save(new Ship(gp9,"Destroyer", Arrays.asList("B5", "C5", "D5")));
			Ship s19 = shipRepository.save(new Ship(gp9,"Patrol Boat", Arrays.asList("C6", "C7")));

			Ship s20 = shipRepository.save(new Ship(gp10,"Submarine", Arrays.asList("A2", "A3", "A4")));
			Ship s21 = shipRepository.save(new Ship(gp10,"Patrol Boat", Arrays.asList("C6", "C7")));

			Ship s22 = shipRepository.save(new Ship(gp11,"Destroyer", Arrays.asList("B5", "C5", "D5")));
			Ship s23 = shipRepository.save(new Ship(gp11,"Patrol Boat", Arrays.asList("C6", "C7")));

			Ship s24 = shipRepository.save(new Ship(gp15,"Destroyer", Arrays.asList("B5", "C5", "D5")));
			Ship s25 = shipRepository.save(new Ship(gp15,"Patrol Boat", Arrays.asList("C6", "C7")));

			Ship s26 = shipRepository.save(new Ship(gp16,"Submarine", Arrays.asList("A2", "A3", "A4")));
			Ship s27 = shipRepository.save(new Ship(gp16,"Patrol Boat", Arrays.asList("G6", "H6")));
			////////

			Salvo sal1 = salvoRepository.save(new Salvo(gp1,1,  Arrays.asList("B5", "C5", "F1")));
			Salvo sal2 = salvoRepository.save(new Salvo(gp2,1,  Arrays.asList("B4", "B5", "B6")));
			Salvo sal3 = salvoRepository.save(new Salvo(gp1,2,  Arrays.asList("F2", "D5")));
			Salvo sal4 = salvoRepository.save(new Salvo(gp2,2,  Arrays.asList("E1", "H3", "A2")));

			Salvo sal5 = salvoRepository.save(new Salvo(gp3,1,  Arrays.asList("A2", "A4", "G6")));
			Salvo sal6 = salvoRepository.save(new Salvo(gp4,1,  Arrays.asList("B5", "D5", "C7")));
			Salvo sal7 = salvoRepository.save(new Salvo(gp3,2,  Arrays.asList("A3", "H6")));
			Salvo sal8 = salvoRepository.save(new Salvo(gp4,2,  Arrays.asList("C5", "C6")));

			Salvo sal9 = salvoRepository.save(new Salvo(gp5,1,  Arrays.asList("G6", "H6", "A4")));
			Salvo sal10 = salvoRepository.save(new Salvo(gp6,1,  Arrays.asList("H1", "H2", "H3")));
			Salvo sal11 = salvoRepository.save(new Salvo(gp5,2,  Arrays.asList("A2", "A3", "D8")));
			Salvo sal12 = salvoRepository.save(new Salvo(gp6,2,  Arrays.asList("E1", "F2", "G3")));

			Salvo sal13 = salvoRepository.save(new Salvo(gp7,1,  Arrays.asList("A3", "A4", "F7")));
			Salvo sal14 = salvoRepository.save(new Salvo(gp8,1,  Arrays.asList("B5", "C6", "H1")));
			Salvo sal15 = salvoRepository.save(new Salvo(gp7,2,  Arrays.asList("A2", "G6", "H6")));
			Salvo sal16 = salvoRepository.save(new Salvo(gp8,2,  Arrays.asList("C5", "C7", "D5")));

			Salvo sal17 = salvoRepository.save(new Salvo(gp9,1,  Arrays.asList("A1", "A2", "A3")));
			Salvo sal18 = salvoRepository.save(new Salvo(gp10,1,  Arrays.asList("B5", "B6", "C7")));
			Salvo sal19 = salvoRepository.save(new Salvo(gp9,2,  Arrays.asList("G6", "G7", "G8")));
			Salvo sal20 = salvoRepository.save(new Salvo(gp10,2,  Arrays.asList("C6", "D6", "E6")));
			Salvo sal21 = salvoRepository.save(new Salvo(gp10,3,  Arrays.asList("H1", "H8")));


			Score sc1 = scoreRepository.save(new Score(1, g1, p1));
			Score sc2 = scoreRepository.save(new Score(0, g1, p2));
			Score sc3 = scoreRepository.save(new Score(0.5, g2, p1));
			Score sc4 = scoreRepository.save(new Score(0.5, g2, p2));
			Score sc5 = scoreRepository.save(new Score(1, g3, p2));
			Score sc6 = scoreRepository.save(new Score(0, g3, p4));
			Score sc7 = scoreRepository.save(new Score(0.5, g4, p2));
			Score sc8 = scoreRepository.save(new Score(0.5, g4, p1));

		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputUserName -> {
			Player player = playerRepository.findByUserName(inputUserName);
			if(player != null){
				return new User(player.getUserName(), player.getPassword(), AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unkown user: " + inputUserName);
			}

		});
	}
}
@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/games").permitAll()
				.antMatchers("/api/players").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/rest/**").hasAuthority("ADMIN")
				.anyRequest().fullyAuthenticated();

		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}

}