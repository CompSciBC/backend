package bmg.service;

import bmg.repository.QRCodeRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Date;

/**
 * Generates QR Codes
 */
@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final QRCodeRepository REPO;
    private final ReservationService R_SVC;

    @Value("${client.route.add-reservation}")
    private String addReservationRoute;

    /**
     * Encodes the given content into a QR code and saves it to the database
     *
     * @param content The content to be encoded in the QR code
     * @param id A unique identifier for the QR code
     * @param size The height/width of the QR code in pixels
     * @throws WriterException If QR encoding fails
     * @throws IOException If unable to save QR code
     */
    private void generate(String content, String id, int size)
            throws WriterException, IOException {

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size);

        Path path = Files.createTempFile("temp-qr", ".png");
        MatrixToImageWriter.writeToPath(matrix, "PNG", path);

        REPO.saveOne(id, path.toFile());
        path.toFile().delete();
    }

    /**
     * Gets a temporary URL for accessing the identified QR code
     *
     * @param id A QR code id
     * @return A temporary URL with a 1-hour expiration
     */
    public URL getURL(String id) {
        boolean newQRCodeNeeded = !REPO.qrCodeExists(id) && R_SVC.findOne(id) != null;

        if (newQRCodeNeeded) {
            try {
                generate(addReservationRoute+id, id, 256);

            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("Unable to generate QR code: "+e+".");
            }
        }
        return REPO.getUrl(id, Date.from(Instant.now().plusSeconds(60*60)));
    }
}
