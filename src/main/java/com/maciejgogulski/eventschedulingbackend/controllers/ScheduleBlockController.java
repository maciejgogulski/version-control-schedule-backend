//package com.maciejgogulski.eventschedulingbackend.controllers;
//
//import com.maciejgogulski.eventschedulingbackend.domain.BlockParameter;
//import com.maciejgogulski.eventschedulingbackend.domain.ScheduleBlock;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Date;
//import java.util.List;
//
//@RestController
//@RequestMapping("/schedule-block")
//public class ScheduleBlockController {
//
//    @GetMapping("/tag/{scheduleTagId}")
//    public List<ScheduleBlock> getBlocksByTag(@PathVariable Long scheduleTagId, @RequestParam Date startDate, @RequestParam Date endDate) {
//        return null;
//    }
//
//    @GetMapping("/{scheduleBlockId}")
//    public ScheduleBlock getScheduleElement(@PathVariable Long scheduleBlockId) {
//        return null;
//    }
//
//    @PostMapping
//    public ScheduleBlock addScheduleBlock(Long scheduleTagId, ScheduleBlock scheduleBlock) {
//        return null;
//    }
//
//    @PutMapping
//    public ScheduleBlock editScheduleBlock(ScheduleBlock scheduleBlock) {
//        return null;
//    }
//
//    @DeleteMapping
//    public String deleteScheduleBlock(Long scheduleBlockId) {
//        return null;
//    }
//
//    @PostMapping
//    public ScheduleBlock addParameterToBlock(BlockParameter blockParameter) {
//        return null;
//    }
//
//}
