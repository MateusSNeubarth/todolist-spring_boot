package br.com.mateusneubarth.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.mateusneubarth.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();

        // Check for tasks route only
        if (servletPath.startsWith("/tasks/")) {
            // Get authentication (user & password)
            var authorization = request.getHeader("Authorization");

            // Trim part of the string to get only the Base64
            var authEncoded = authorization.substring("Basic".length()).trim();

            // Decode Base64 into byte array
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

            // Turn byte array into String
            var authString = new String(authDecoded);

            // Split username and password to different arrays
            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            // Validate user
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                // Not valid user, throw unauthorized
                response.sendError(401);
            } else {
                // Validate password
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    // Set userId
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    // Not valid password, throw unauthorized
                    response.sendError(401);
                }

            }
        } else {
            // Not in tasks route, continue
            filterChain.doFilter(request, response);
        }

    }

}
