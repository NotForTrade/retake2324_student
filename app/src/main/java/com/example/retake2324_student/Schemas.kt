package com.example.retake2324_student

import android.graphics.Bitmap
import com.example.retake2324_student.Schemas.Users.bindTo
import com.example.retake2324_student.Schemas.Users.primaryKey
import com.example.retake2324_student.Schemas.Users.references
import org.ktorm.dsl.toDouble
import org.ktorm.entity.Entity
import org.ktorm.schema.*


interface Announcement : Entity<Announcement> {
    companion object : Entity.Factory<Announcement>()
    var id: Int
    var tutor: User
    var title: String
    var content: String
    var datetime: String
}

interface AttendanceState : Entity<AttendanceState> {
    companion object : Entity.Factory<AttendanceState>()
    var id:  Int
    var value: String
}

interface Attendance : Entity<Attendance> {
    companion object : Entity.Factory<Attendance>()
    var id:  Int
    var tutor: User
    var student: User
    var component: Component
    var status: AttendanceState
    var session: Int
}

interface Component : Entity<Component> {
    companion object : Entity.Factory<Component>()
    var id:  Int
    var module: Module
    var tutor: User
    var name: String
    var skills: List<Skill>
    var scores: List<StudentSkillMapping>
}

interface Module : Entity<Module> {
    companion object : Entity.Factory<Module>()
    var id:  Int
    var name: String
}

interface Role : Entity<Role> {
    companion object : Entity.Factory<Role>()
    var id:  Int
    var name: String
}


interface Group : Entity<Group> {
    companion object : Entity.Factory<Group>()
    var id:  Int
    var name: String
}

interface User : Entity<User> {
    companion object : Entity.Factory<User>()
    var id: Int
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
    var id:  Int
    var component: Component
    var name: String
    var description: String
    var coefficient: Int
    var scores: List<StudentSkillMapping>
}

interface GroupSkillMapping : Entity<GroupSkillMapping> {
    companion object : Entity.Factory<GroupSkillMapping>()
    var id:  Int
    var skill: Skill
    var group: Group
    var observation: String
}

interface StudentSkillMapping : Entity<StudentSkillMapping> {
    companion object : Entity.Factory<StudentSkillMapping>()
    var id:  Int
    var student: User
    var skill: Skill
    var score: Double
    var observation: String
    var document: String
}

object Schemas {

    object Announcements : Table<Announcement>("announcement") {
        val Id = int("id").primaryKey().bindTo { it.id }
        val TutorId = int("tutor_id").references(Users) { it.tutor }
        val Title = varchar("title").bindTo { it.title }
        val Content = varchar("content").bindTo { it.content }
        val DateTime = varchar("datetime").bindTo { it.datetime }
    }

    object AttendanceStates : Table<AttendanceState>("attendance_status") {
        val Id = int("id").primaryKey().bindTo { it.id}
        val Value = varchar("value").bindTo { it.value }
    }

    object Attendances : Table<Attendance>("attendance") {
        val Id = int("id").primaryKey().bindTo { it.id}
        val ComponentId = int("component_id").references(Components) { it.component }
    }

    object Roles : Table<Role>("role") {
        val Id = int("id").primaryKey().bindTo { it.id}
        val Name = varchar("name").bindTo { it.name }
    }

    object Components : Table<Component>("component") {
        val Id = int("id").primaryKey().bindTo { it.id}
        val Name = varchar("name").bindTo { it.name }
    }

    object Groups : Table<Group>("group") {
        val Id = int("id").primaryKey().bindTo { it.id}
        val Name = varchar("name").bindTo { it.name }
    }

    object Modules : Table<Module>("module") {
        val Id = int("id").primaryKey().bindTo { it.id}
        val Name = varchar("name").bindTo { it.name }
    }

    object Users : Table<User>("user") {
        val Id = int("id").primaryKey().bindTo { it.id }
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
        val Id = int("id").primaryKey().bindTo { it.id }
        val ComponentId = int("component_id").references(Components) { it.component }
        val Name = varchar("name").bindTo { it.name }
        val Description = varchar("description").bindTo { it.description }
        val Coefficient = int("coefficient").bindTo { it.coefficient }
    }

    object GroupSkillMappings : Table<GroupSkillMapping>("group_skill_mapping") {
        val Id = int("id").primaryKey().bindTo { it.id}
        val SkillId = int("skill_id").references(Skills) { it.skill }
        val GroupId = int("group_id").references(Groups) { it.group }
        val Observation = varchar("observation").bindTo { it.observation }
    }
    object StudentSkillMappings : Table<StudentSkillMapping>("student_skill_mapping") {
        val Id = int("id").primaryKey().bindTo { it.id}
        val StudentId = int("student_id").references(Users) { it.student }
        val SkillId = int("skill_id").references(Skills) { it.skill }
        val score = double("score").bindTo { it.score }
        val Observation = varchar("observation").bindTo { it.observation }
        val Document = varchar("document").bindTo { it.document }
    }

}