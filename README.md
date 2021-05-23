# :date: Digital Scheduler - by LadyKillas, VinUniversity

![image](https://user-images.githubusercontent.com/84661482/119254719-26081900-bbe2-11eb-9ffb-31e31866a999.png)

:bulb: A Java-based desktop application, created from scratch with no plagiarism 😉, seasoned with a combination of CSS and MySQL.    

:toolbox: The app allows users to input their busy schedule, and will automatically generate free timeslots for 2+ people to have a meeting of a specific duration.     
  
:computer: Developed as the final project of the course "Object Oriented Programming, Algorithm and Data Structures" - COMP1020, VinUniversity     

## :brain: Our team:    
* Nguyen Dinh Cuong: Front-end & Back-end developer   
* Pham Duc "Oreo-Hippie" Hiep: Database designer & Back-end developer & Overlooking the entire project 
* Dau Vu Dang "DVD" Khoi: Core algorithm designer

## :gear: Features:
- Log in and registration required
- Create tasks and put them on your daily schedule.
- Edit your tasks (name, task type, start time, end time, etc)
- Delete your tasks
- Generate a meeting with other people using an efficient algorithm
    
## :camera_flash: Screenshots:
<p align="center">
    <img width="400" height="281" src="https://user-images.githubusercontent.com/84661482/119264175-3cc36580-bc0c-11eb-8705-df919c537d4d.png">
    <img width="400" height="281" src="https://user-images.githubusercontent.com/84661482/119264211-611f4200-bc0c-11eb-8991-0ffc17b051c9.png">
    <img width="400" height="281" src="https://user-images.githubusercontent.com/84661482/119264352-0508ed80-bc0d-11eb-8e83-5ae5339667af.png">
    <img width="400" height="281" src="https://user-images.githubusercontent.com/84661482/119264403-3e415d80-bc0d-11eb-88f9-86575389c641.png">
    <img width="400" height="281" src="https://user-images.githubusercontent.com/84661482/119264576-fbcc5080-bc0d-11eb-9527-076edf8760a4.png">
</p>

## :floppy_disk: Database design:   
![image](https://user-images.githubusercontent.com/76086897/119266828-84e78580-bc16-11eb-8974-00a94f4daa46.png)


## :round_pushpin: Algorithm used for generating calendar:
![image](https://user-images.githubusercontent.com/84661482/119267158-a09f5b80-bc17-11eb-9d6c-be8e98826fb9.png)
- Create a two-dimensional linked list. Each linked list in the main linked list is the busy calendar of one person.
- First, pop two linked lists to merge them to one busy calendar. After that, pop one linked list from the main linked list to continuously merge to be one busy calendar unitl the main linked list is empty.
- Consider the last busy calendar as the general busy calendar of all people.
- From the last busy calendar, it is possible to set up meeting schedule for everyone.

