package bmg.controller;

import bmg.dto.Guidebook;
import bmg.service.GuidebookService;
import lombok.RequiredArgsConstructor;
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
     * POST request for /api/guidebook/PID#######/images
     * @param id PropertyID
     * @param gb JSON file of type Guidebook. Includes optional and required fields
     * @return a String denoting if saved successfully to S3
     */
    @PostMapping("/{id}/content")
    public String saveGuidebookContent(@PathVariable(name = "id") String id, @RequestBody Guidebook gb) {
        return SVC.saveGbContentToS3(id, gb);
    }

    /**
     * GET request for /api/guidebook/PID#######/content
     * @param id PropertyID
     * @return a Guidebook JSON object for that particular property
     * @throws IOException
     */
    @GetMapping("/{id}/content")
    public Guidebook retrieveGuidebookContent(@PathVariable(name = "id") String id) throws IOException {
        return SVC.retrieveGbContentFromS3(id);
    }

    /**
     * POST request for /api/guidebook/PID#######/images
     * When making a request, set the 'Content-Type' Header, to 'multipart/form-data'
     * with form-data as body, with 'files' as Key, and Value 'MultipartFile' (Mult. image files)
     * @param id PropertyID
     * @param files MultiPartFile of multiple image files
     * @return a List of Strings of the uniqueobjectkey for each image saved in this request in S3
     * @throws IOException
     */
    @PostMapping("/{id}/images")
    public List<String> uploadGuidebookImages(@PathVariable(name = "id") String id, @RequestParam("files") MultipartFile[] files) throws IOException {
        return SVC.saveGbImagesToS3(id, files);
    }

    /**
     * GET request for /api/guidebook/PID#######/images
     * @param id PropertyID
     * @return a List of presigned URL strings from AWS S3
     */
    @GetMapping("/{id}/images")
    public List<String> getImagesFromS3(@PathVariable(name = "id") String id) {
        return SVC.retrieveGbImagesFromS3(id);
    }

    /**
     * DELETE request for /api/guidebook/PID#######/delete
     * @param id PropertyID
     */
    @GetMapping("/{id}/delete")
    public void deleteGuidebook(@PathVariable(name = "id") String id) {
        SVC.deleteGuidebook(id);
    }
}