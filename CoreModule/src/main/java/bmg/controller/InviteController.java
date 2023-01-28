package bmg.controller;

import bmg.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.List;

/**
 * Handles requests for invite codes
 */
@RestController
@RequestMapping("/api/invites")
@RequiredArgsConstructor
public class InviteController extends Controller<URL> {

    private final QRCodeService SVC;

    /**
     * Gets a temporary URL to access the invite code with the given id
     *
     * @param id A QR code id
     * @return A response entity containing a URL to the QR code
     */
    @GetMapping("/{id}")
    public ResponseEntity<Response<URL>> getURL(@PathVariable(name = "id") String id) {
        return responseCodeOk(List.of(SVC.getURL(id)));
    }
}
