package bmg.controller;

import bmg.dto.Guidebook;
import bmg.service.GuidebookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/api/guidebook")
@RequiredArgsConstructor
public class GuidebookController extends Controller<Guidebook> {

    private final GuidebookService SVC;

    /**
     *
     * @param id
     * @param gb
     * @return
     */
    @PostMapping("/{id}/content")
    public String saveGuidebookContent(@PathVariable(name = "id") String id, @RequestBody Guidebook gb) {
        return SVC.saveGbContentToS3(id, gb);
    }

    /**
     *
     * @param id
     * @return
     * @throws IOException
     */
    @GetMapping("/{id}/content")
    public Guidebook retrieveGuidebookContent(@PathVariable(name = "id") String id) throws IOException {
        return SVC.retrieveGbContentFromS3(id);
    }

    /**
     *
     * When making a request, set the 'Content-Type' Header, to 'multipart/form-data' to
     * @param id
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping("/{id}/images")
    public List<String> uploadGuidebookImages(@PathVariable(name = "id") String id, @RequestParam("files") MultipartFile[] files) throws IOException {
        return SVC.saveGbImagesToS3(id, files);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/images")
    public List<String> getImagesFromS3(@PathVariable(name = "id") String id) {
        return SVC.retrieveGbImagesFromS3(id);
    }

    /**
     *
     * @param id
     */
    @GetMapping("/{id}/delete")
    public void deleteGuidebook(@PathVariable(name = "id") String id) {
        SVC.deleteGuidebook(id);
    }
}