package Controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import DAO.AccountDAO;
import DAO._ConstraintDAO;
import DAO.MessageDAO;
import Service.AccountService;
import Service.MessageService;
import Service.MessageTextService;
import Util.ConnectionUtil;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.UnauthorizedResponse;

/**
 * You will need to write your own endpoints and handlers for your controller.
 * The endpoints you will need can be found in readme.md as well as the test
 * cases. You should refer to prior mini-project labs and lecture materials for
 * guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints
     * in the startAPI() method, as the test suite must receive a Javalin object
     * from this method.
     *
     * @return a Javalin app object which defines the behavior of the Javalin
     *         controller.
     */
    public Javalin startAPI() {
        // Add check constraints we need to the database
        // This should never happen in production code unless you
        // are instantiating your entire database when the API starts.
        // Your database should be properly configured ahead of time
        try (Connection conn = ConnectionUtil.getConnection()) {
            _ConstraintDAO.addAccountUsernameEmptyString(conn);
            _ConstraintDAO.addMessageTextEmptyString(conn);
            _ConstraintDAO.addPasswordMinimumLength(conn, 4);
        } catch (SQLException e) {
            throw new RuntimeException("API start failed to init", e);
        }

        Javalin app = Javalin.create();
        // Unit tests require we throw blank messages in error responses
        app.exception(BadRequestResponse.class, (e, ctx) -> ctx.status(400));
        app.exception(UnauthorizedResponse.class, (e, ctx) -> ctx.status(401));

        // URL param parsing is done by Java wrappers whose throws need to be handled
        app.exception(NumberFormatException.class, (e, ctx) -> {
            throw new BadRequestResponse(e.getMessage());
        });
        // Jackson throws its own exceptions when JSON parsing fails that we must catch
        app.exception(ValueInstantiationException.class, (e, ctx) -> {
            throw new BadRequestResponse(e.getMessage());
        });
        // SQL constraint and data violations are the users fault
        app.exception(SQLException.class, (e, ctx) -> {
            // SQLState 23 indicates constraint violation
            // SQLState 22 indicates data violation
            if (e.getSQLState().startsWith("23") || e.getSQLState().startsWith("22")) {
                throw new BadRequestResponse(e.getMessage());
            }
            ctx.status(500).result(e.getMessage());
        });

        // User Registration
        app.post("register", ctx -> ctx
                .json(ctx
                        .bodyAsClass(AccountService.class)
                        .register(ConnectionUtil.getConnection())));

        // User Login
        app.post("login", ctx -> {
            Optional<AccountDAO> login = ctx
                    .bodyAsClass(AccountService.class)
                    .login(ConnectionUtil.getConnection());
            if (login.isPresent()) {
                ctx.json(login.get());
            } else {
                throw new UnauthorizedResponse("username and password must exist");
            }
        });

        // Create Message
        app.post("messages",
                ctx -> ctx.json(ctx
                        .bodyAsClass(MessageService.class)
                        .create(ConnectionUtil.getConnection())));

        // Retrieve All Messages
        app.get("messages", ctx -> ctx
                .json(MessageDAO.select(ConnectionUtil.getConnection())));

        // Retrieve Message
        app.get("messages/{message_id}", ctx -> {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Optional<MessageDAO> message = MessageDAO
                    .fetch(ConnectionUtil.getConnection(), messageId);
            if (message.isPresent()) {
                ctx.json(message.get());
            } else {
                ctx.status(200);
            }
        });

        // Delete Message
        app.delete("messages/{message_id}", ctx -> {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Optional<MessageDAO> message = MessageDAO
                    .fetch(ConnectionUtil.getConnection(), messageId);
            if (message.isPresent()) {
                MessageDAO.delete(ConnectionUtil.getConnection(), messageId);
                ctx.json(message.get());
            } else {
                ctx.status(200);
            }
        });

        // Retrieve All User Messages
        app.get("accounts/{account_id}/messages", ctx -> {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            ctx.json(MessageDAO
                    .select(ConnectionUtil.getConnection(), accountId));
        });

        // Update Message Text
        app.patch("messages/{messageId}", ctx -> {
            int messageId = Integer.parseInt(ctx.pathParam("messageId"));
            Optional<MessageDAO> message = ctx.bodyAsClass(MessageTextService.class)
                    .update(ConnectionUtil.getConnection(), messageId);
            if (message.isPresent()) {
                ctx.json(message.get());
            } else {
                throw new BadRequestResponse("Update not successful");
            }
        });

        return app;
    }

}