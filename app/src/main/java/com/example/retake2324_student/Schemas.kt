package com.example.retake2324_student

import android.graphics.Bitmap
import com.example.retake2324_student.Schemas.Users.bindTo
import com.example.retake2324_student.Schemas.Users.primaryKey
import com.example.retake2324_student.Schemas.Users.references
import org.ktorm.entity.Entity
import org.ktorm.schema.*


interface Announcement : Entity<Announcement> {
    companion object : Entity.Factory<Announcement>()
    var announcementId: Int
    var tutor: User
    var title: String
    var content: String
    var datetime: String
}

interface AttendanceState : Entity<AttendanceState> {
    companion object : Entity.Factory<AttendanceState>()
    var attendanceStateId:  Int
    var attendanceStateName: String
}

interface Attendance : Entity<Attendance> {
    companion object : Entity.Factory<Attendance>()
    var attendanceId:  Int
    var tutor: User
    var student: User
    var component: Component
    var status: AttendanceState
    var session: Int
}

interface Component : Entity<Component> {
    companion object : Entity.Factory<Component>()
    var componentId:  Int
    var module: Module
    var tutor: User
    var componentName: String
}

interface Module : Entity<Module> {
    companion object : Entity.Factory<Module>()
    var moduleId:  Int
    var moduleName: String
}

interface Role : Entity<Role> {
    companion object : Entity.Factory<Role>()
    var roleId:  Int
    var roleName: String
}


interface Group : Entity<Group> {
    companion object : Entity.Factory<Group>()
    var groupId:  Int
    var groupName: String
}

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    var userId: Int
    var role: Role
    var group: Group
    var module: Module
    var component: Component
    var photo: String
    var firstName: String
    var lastName: String
    var email: String
    var password: String

}

interface Skill : Entity<Skill> {
    companion object : Entity.Factory<Skill>()
    var skillId:  Int
    var component: Component
    var skillName: String
    var description: String
    var coefficient: Int
}

interface GroupSkillMapping : Entity<GroupSkillMapping> {
    companion object : Entity.Factory<GroupSkillMapping>()
    var groupSkillMappingId:  Int
    var skill: Skill
    var group: Group
    var observation: String
}

interface StudentSkillMapping : Entity<StudentSkillMapping> {
    companion object : Entity.Factory<StudentSkillMapping>()
    var studentSkillMappingId:  Int
    var user: User
    var skill: Skill
    var score: Int
    var observation: String
    var document: String
}

object Schemas {

    object Announcements : Table<Announcement>("announcement") {
        val AnnouncementId = int("id").primaryKey().bindTo { it.announcementId }
        val TutorId = int("tutor_id").references(Users) { it.tutor }
        val Title = varchar("title").bindTo { it.title }
        val Content = varchar("content").bindTo { it.content }
        val DateTime = varchar("datetime").bindTo { it.datetime }
    }

    object AttendanceStates : Table<AttendanceState>("attendance_status") {
        val AttendanceStateId = int("id").primaryKey().bindTo { it.attendanceStateId}
        val AttendanceStateName = varchar("value").bindTo { it.attendanceStateName }
    }

    object Attendances : Table<Attendance>("attendance") {
        val AttendanceId = int("id").primaryKey().bindTo { it.attendanceId}
        val ComponentId = int("component_id").references(Components) { it.component }
    }

    object Roles : Table<Role>("role") {
        val RoleId = int("id").primaryKey().bindTo { it.roleId}
        val RoleName = varchar("role_name").bindTo { it.roleName }
    }

    object Components : Table<Component>("component") {
        val ComponentId = int("id").primaryKey().bindTo { it.componentId}
        val ComponentName = varchar("component_name").bindTo { it.componentName }
    }

    object Groups : Table<Group>("group") {
        val GroupId = int("id").primaryKey().bindTo { it.groupId}
        val GroupName = varchar("group_name").bindTo { it.groupName }
    }

    object Modules : Table<Module>("module") {
        val ModuleId = int("id").primaryKey().bindTo { it.moduleId}
        val ModuleName = varchar("module_name").bindTo { it.moduleName }
    }

    object Users : Table<User>("user") {
        val UserId = int("id").primaryKey().bindTo { it.userId }
        val RoleId = int("role_id").references(Roles) { it.role }
        val GroupId = int("group_id").references(Groups) { it.group }
        val ModuleId = int("module_id").references(Modules) { it.module}
        val ComponentId = int("component_id").references(Components) { it.component }
        val Photo = varchar("photo").bindTo {it.photo}
        val FirstName = varchar("first_name").bindTo { it.firstName }
        val LastName = varchar("last_name").bindTo { it.lastName }
        val Mail = varchar("email").bindTo { it.email }
        val Password = varchar("password").bindTo { it.password }
    }

    object Skills : Table<Skill>("skill") {
        val SkillId = int("id").primaryKey().bindTo { it.skillId }
        val ComponentId = int("component_id").references(Components) { it.component }
        val SkillName = varchar("name").bindTo { it.skillName }
        val Description = varchar("description").bindTo { it.description }
        val Coefficient = int("coefficient").bindTo { it.coefficient }
    }

    object GroupSkillMappings : Table<GroupSkillMapping>("group_skill_mapping") {
        val GroupSkillMappingId = int("id").primaryKey().bindTo { it.groupSkillMappingId}
        val SkillId = int("skill_id").references(Skills) { it.skill }
        val GroupId = int("group_id").references(Groups) { it.group }
        val Observation = varchar("observation").bindTo { it.observation }
    }
    object StudentSkillMappings : Table<StudentSkillMapping>("student_skill_mapping") {
        val StudentSkillMappingId = int("id").primaryKey().bindTo { it.studentSkillMappingId}
        val UserId = int("user_id").references(Users) { it.user }
        val Skill = int("skill_id").references(Skills) { it.skill }
        val score = int("score").bindTo { it.score }
        val Observation = varchar("observation").bindTo { it.observation }
        val Document = varchar("document").bindTo { it.document }
    }

}