package com.quantumSyntax.journalApp.controller;

import com.quantumSyntax.journalApp.entity.JournalEntry;
import com.quantumSyntax.journalApp.service.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


// Rest controller annotation is used to create beans/component
@RestController
@RequestMapping("/journal")  // add mapping to all
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService; // create object from IoC


//    private Map<Long, JournalEntry> journalEntries = new HashMap<>();

    // create endpoint for finding all journal present in database
    @GetMapping
    public ResponseEntity<?> getAll(){
        List<JournalEntry> allJournal = journalEntryService.getAll();
        if(allJournal!=null && !allJournal.isEmpty()) return new ResponseEntity<>(allJournal, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> createEntry(@RequestBody JournalEntry myEntry){
        try{
            myEntry.setDate(LocalDateTime.now());
            journalEntryService.saveEntry(myEntry);
            return new ResponseEntity<>(myEntry, HttpStatus.CREATED);
        }catch (Exception error){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // for health checkup
    @GetMapping("/health-check")
    public String healthCheckUp(){
        return "Ok";
    }


    @GetMapping("id/{myId}")
    public ResponseEntity< JournalEntry> getJournalEntryById(@PathVariable ObjectId myId){
        Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
        if(journalEntry.isPresent()){
            return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("id/{myId}")
    public ResponseEntity<?> deleteJournalById(@PathVariable ObjectId myId){
        JournalEntry oldEntry = journalEntryService.findById(myId).orElse(null);
        if(oldEntry==null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        journalEntryService.delelteById(myId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/id/{myId}")
    public ResponseEntity<?> updateJournalById(@PathVariable ObjectId myId, @RequestBody JournalEntry newEntry){
        // find old entry
        JournalEntry oldEntry = journalEntryService.findById(myId).orElse(null);
        if(oldEntry!=null){
            oldEntry.setTitle(newEntry.getTitle() != null&& !newEntry.getTitle().isEmpty() ?newEntry.getTitle():oldEntry.getTitle());
            oldEntry.setContent(newEntry.getContent()!=null && !newEntry.getContent().isEmpty()?newEntry.getContent():oldEntry.getContent());
            // upto now we set value only not saved
            journalEntryService.saveEntry(oldEntry);
            return new ResponseEntity<>(oldEntry, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }
}
