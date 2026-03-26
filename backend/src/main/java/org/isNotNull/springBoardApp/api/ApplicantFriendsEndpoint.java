package org.isNotNull.springBoardApp.api;

import org.isNotNull.springBoardApp.service.AppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Applicant endpoints for friends and networking.
 */
@RestController
@RequestMapping("/v1/applicant")
public final class ApplicantFriendsEndpoint {

    private final AppService app;

    public ApplicantFriendsEndpoint(final AppService app) {
        this.app = app;
    }

    @GetMapping("/friends")
    public List<ViewJson.Friend> friends() {
        return this.app.myFriends();
    }

    @GetMapping("/friends/requests")
    public List<ViewJson.Friend> pendingRequests() {
        return this.app.pendingFriendRequests();
    }

    @GetMapping("/friends/sent")
    public List<ViewJson.Friend> sentRequests() {
        return this.app.sentFriendRequests();
    }

    @PostMapping("/friends/{userId}")
    public void sendRequest(@PathVariable final String userId) {
        this.app.sendFriendRequest(userId);
    }

    @PostMapping("/friends/{userId}/accept")
    public void acceptRequest(@PathVariable final String userId) {
        this.app.acceptFriendRequest(userId);
    }

    @PostMapping("/friends/{userId}/reject")
    public void rejectRequest(@PathVariable final String userId) {
        this.app.rejectFriendRequest(userId);
    }

    @DeleteMapping("/friends/{userId}")
    public void removeFriend(@PathVariable final String userId) {
        this.app.removeFriend(userId);
    }

    @DeleteMapping("/friends/{userId}/cancel")
    public void cancelRequest(@PathVariable final String userId) {
        this.app.cancelFriendRequest(userId);
    }

    @GetMapping("/friends/{userId}/status")
    public ViewJson.FriendStatus friendStatus(@PathVariable final String userId) {
        return this.app.getFriendStatus(userId);
    }

    @GetMapping("/users/search")
    public List<ViewJson.User> searchUsers(@RequestParam final String q) {
        return this.app.searchUsers(q);
    }

    @GetMapping("/users/{userId}")
    public ViewJson.UserProfile userProfile(@PathVariable final String userId) {
        return this.app.userProfile(userId);
    }
}