package bmg.controller;

import bmg.dto.Guidebook;
import bmg.model.Property;
import bmg.service.GuidebookService;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/guidebook")
@RequiredArgsConstructor
public class GuidebookController extends Controller<Guidebook> {

    private final GuidebookService SVC;

    @PostMapping("/{id}")
    public String saveGuidebook(@PathVariable(name = "id") String id, @RequestBody Guidebook gb) {
        return SVC.saveToS3(id, gb);
    }

    @GetMapping("/{id}")
    public Guidebook retrieveGuidebook(@PathVariable(name = "id") String id) throws IOException {
        return SVC.retrieveFromS3(id);
    }

}