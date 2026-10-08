package com.fitness.activityservice.service;

import com.fitness.activityservice.DTO.ActivityRequest;
import com.fitness.activityservice.DTO.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActiveRepository activeRepository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityResponse trackActivity(ActivityRequest request) {
        try{
            boolean isValidUser = userValidationService.validateUser(request.getUserId());
            if(!isValidUser){
                throw new RuntimeException("Invalid User: "+request.getUserId());
            }
        }catch(Exception e){
            log.info("Error in userValidationService {}",e.getMessage());
        }

        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration((request.getDuration()))
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additonalMetrics(request.getAdditionalMetrics())
                .build();

        Activity savedActivity = activeRepository.save(activity);

        //send the saved Activity to Rabbit MQ
        //that is published to rabbit mq for Artificial Intelligence service

        try{
            rabbitTemplate.convertAndSend(exchange,routingKey,savedActivity);
            log.info("activity published to RabbitMQ");
        }catch(Exception e){
            log.error("Failed to publish activity to RabbitMQ : {}",e);
        }

        return mapToResponse(savedActivity);
    }

    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse response = new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setStartTime(activity.getStartTime());
        response.setAdditonalMetrics(activity.getAdditonalMetrics());
        response.setCreatedAt(activity.getCreatedAt());
        response.setUpdatedAt(activity.getUpdatedAt());

        return response;
    }

    public List<ActivityResponse> getUserActivities(String userId) {
        List<Activity> activities = activeRepository.findByUserId(userId);

        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

    }

    public ActivityResponse getActivityById(String id) {
        return activeRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                    new RuntimeException("Activity not found with this Id :" + id)
                );
    }
}
