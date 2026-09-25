-- Reference data: the permissions the code checks, and the default roles that bundle them.
-- New permissions arrive in new migrations as new modules are built.

INSERT INTO permissions (code, module, description) VALUES
    ('USER_VIEW',           'USER',        'View user accounts'),
    ('USER_CREATE',         'USER',        'Create user accounts'),
    ('USER_UPDATE',         'USER',        'Update user accounts'),
    ('USER_DEACTIVATE',     'USER',        'Activate or deactivate user accounts'),
    ('ROLE_VIEW',           'USER',        'View roles and their permissions'),
    ('ROLE_ASSIGN',         'USER',        'Assign roles to users'),
    ('ROLE_MANAGE',         'USER',        'Create and edit roles'),

    ('ORG_VIEW',            'ORGANIZATION','View organization structure'),
    ('ORG_MANAGE',          'ORGANIZATION','Manage divisions, departments, teams and positions'),

    ('EMPLOYEE_VIEW',       'EMPLOYEE',    'View employee profiles'),
    ('EMPLOYEE_CREATE',     'EMPLOYEE',    'Create employees'),
    ('EMPLOYEE_UPDATE',     'EMPLOYEE',    'Update employee profiles'),
    ('EMPLOYEE_DELETE',     'EMPLOYEE',    'Delete employees'),

    ('LEAVE_VIEW',          'LEAVE',       'View leave requests'),
    ('LEAVE_APPLY',         'LEAVE',       'Apply for leave'),
    ('LEAVE_APPROVE',       'LEAVE',       'Approve leave requests'),
    ('LEAVE_REJECT',        'LEAVE',       'Reject leave requests'),

    ('ATTENDANCE_VIEW',     'ATTENDANCE',  'View attendance'),
    ('ATTENDANCE_MODIFY',   'ATTENDANCE',  'Correct attendance records'),

    ('PAYROLL_VIEW',        'PAYROLL',     'View payroll and payslips'),
    ('PAYROLL_PROCESS',     'PAYROLL',     'Run payroll'),
    ('PAYROLL_APPROVE',     'PAYROLL',     'Approve payroll runs'),

    ('PERFORMANCE_VIEW',    'PERFORMANCE', 'View performance reviews'),
    ('PERFORMANCE_REVIEW',  'PERFORMANCE', 'Write performance reviews'),
    ('PERFORMANCE_APPROVE', 'PERFORMANCE', 'Approve performance reviews'),

    ('RECRUITMENT_VIEW',    'RECRUITMENT', 'View job openings and candidates'),
    ('RECRUITMENT_MANAGE',  'RECRUITMENT', 'Manage job openings and candidates'),
    ('INTERVIEW_CONDUCT',   'RECRUITMENT', 'Record interview feedback'),

    ('ASSET_VIEW',          'ASSET',       'View company assets'),
    ('ASSET_MANAGE',        'ASSET',       'Assign and manage company assets');

INSERT INTO roles (code, name, description, system_role) VALUES
    ('SUPER_ADMIN',     'Super Admin',     'Unrestricted access',                          TRUE),
    ('SYSTEM_ADMIN',    'System Admin',    'Manages user accounts and roles',              TRUE),
    ('HR_ADMIN',        'HR Admin',        'Full control of HR data',                      TRUE),
    ('HR_MANAGER',      'HR Manager',      'Manages employees and approves HR requests',   TRUE),
    ('HR_EXECUTIVE',    'HR Executive',    'Day-to-day HR operations',                     TRUE),
    ('DEPARTMENT_HEAD', 'Department Head', 'Leads a department',                           TRUE),
    ('PROJECT_MANAGER', 'Project Manager', 'Manages project members',                      TRUE),
    ('TEAM_LEAD',       'Team Lead',       'Leads a team',                                 TRUE),
    ('EMPLOYEE',        'Employee',        'Every staff member',                           TRUE),
    ('FINANCE_ADMIN',   'Finance Admin',   'Approves financial operations',                TRUE),
    ('PAYROLL_ADMIN',   'Payroll Admin',   'Runs payroll',                                 TRUE),
    ('RECRUITER',       'Recruiter',       'Manages hiring pipeline',                      TRUE),
    ('INTERVIEWER',     'Interviewer',     'Interviews candidates',                        TRUE),
    ('IT_ADMIN',        'IT Admin',        'Manages company assets',                       TRUE);

-- Role -> permission mapping. The pattern is matched with LIKE, so 'EMPLOYEE_%' means every EMPLOYEE_* permission.
-- A role grants WHAT you may do; WHOSE data you may do it to (self, team, department, all)
-- is decided later by the owning service from the org hierarchy.
INSERT INTO role_permissions (role_id, permission_id)
SELECT DISTINCT r.id, p.id
FROM (VALUES
    ('SUPER_ADMIN',     '%'),

    ('SYSTEM_ADMIN',    'USER_%'),
    ('SYSTEM_ADMIN',    'ROLE_%'),
    ('SYSTEM_ADMIN',    'ORG_VIEW'),
    ('SYSTEM_ADMIN',    'EMPLOYEE_VIEW'),

    ('HR_ADMIN',        'EMPLOYEE_%'),
    ('HR_ADMIN',        'ORG_%'),
    ('HR_ADMIN',        'LEAVE_%'),
    ('HR_ADMIN',        'ATTENDANCE_%'),
    ('HR_ADMIN',        'PERFORMANCE_VIEW'),
    ('HR_ADMIN',        'RECRUITMENT_VIEW'),
    ('HR_ADMIN',        'USER_VIEW'),
    ('HR_ADMIN',        'USER_CREATE'),
    ('HR_ADMIN',        'ROLE_VIEW'),
    ('HR_ADMIN',        'ROLE_ASSIGN'),

    ('HR_MANAGER',      'EMPLOYEE_VIEW'),
    ('HR_MANAGER',      'EMPLOYEE_CREATE'),
    ('HR_MANAGER',      'EMPLOYEE_UPDATE'),
    ('HR_MANAGER',      'ORG_VIEW'),
    ('HR_MANAGER',      'LEAVE_VIEW'),
    ('HR_MANAGER',      'LEAVE_APPROVE'),
    ('HR_MANAGER',      'LEAVE_REJECT'),
    ('HR_MANAGER',      'ATTENDANCE_%'),
    ('HR_MANAGER',      'PERFORMANCE_VIEW'),
    ('HR_MANAGER',      'PERFORMANCE_APPROVE'),

    ('HR_EXECUTIVE',    'EMPLOYEE_VIEW'),
    ('HR_EXECUTIVE',    'EMPLOYEE_CREATE'),
    ('HR_EXECUTIVE',    'EMPLOYEE_UPDATE'),
    ('HR_EXECUTIVE',    'ORG_VIEW'),
    ('HR_EXECUTIVE',    'LEAVE_VIEW'),
    ('HR_EXECUTIVE',    'ATTENDANCE_VIEW'),

    ('DEPARTMENT_HEAD', 'EMPLOYEE_VIEW'),
    ('DEPARTMENT_HEAD', 'ORG_VIEW'),
    ('DEPARTMENT_HEAD', 'LEAVE_VIEW'),
    ('DEPARTMENT_HEAD', 'LEAVE_APPROVE'),
    ('DEPARTMENT_HEAD', 'LEAVE_REJECT'),
    ('DEPARTMENT_HEAD', 'ATTENDANCE_VIEW'),
    ('DEPARTMENT_HEAD', 'PERFORMANCE_%'),

    ('PROJECT_MANAGER', 'EMPLOYEE_VIEW'),
    ('PROJECT_MANAGER', 'ORG_VIEW'),
    ('PROJECT_MANAGER', 'LEAVE_VIEW'),
    ('PROJECT_MANAGER', 'ATTENDANCE_VIEW'),
    ('PROJECT_MANAGER', 'PERFORMANCE_VIEW'),
    ('PROJECT_MANAGER', 'PERFORMANCE_REVIEW'),

    ('TEAM_LEAD',       'EMPLOYEE_VIEW'),
    ('TEAM_LEAD',       'ORG_VIEW'),
    ('TEAM_LEAD',       'LEAVE_VIEW'),
    ('TEAM_LEAD',       'LEAVE_APPROVE'),
    ('TEAM_LEAD',       'LEAVE_REJECT'),
    ('TEAM_LEAD',       'ATTENDANCE_VIEW'),
    ('TEAM_LEAD',       'PERFORMANCE_VIEW'),
    ('TEAM_LEAD',       'PERFORMANCE_REVIEW'),

    ('EMPLOYEE',        'EMPLOYEE_VIEW'),
    ('EMPLOYEE',        'ORG_VIEW'),
    ('EMPLOYEE',        'LEAVE_VIEW'),
    ('EMPLOYEE',        'LEAVE_APPLY'),
    ('EMPLOYEE',        'ATTENDANCE_VIEW'),
    ('EMPLOYEE',        'PAYROLL_VIEW'),
    ('EMPLOYEE',        'PERFORMANCE_VIEW'),
    ('EMPLOYEE',        'ASSET_VIEW'),

    ('FINANCE_ADMIN',   'PAYROLL_VIEW'),
    ('FINANCE_ADMIN',   'PAYROLL_APPROVE'),
    ('FINANCE_ADMIN',   'EMPLOYEE_VIEW'),

    ('PAYROLL_ADMIN',   'PAYROLL_VIEW'),
    ('PAYROLL_ADMIN',   'PAYROLL_PROCESS'),
    ('PAYROLL_ADMIN',   'EMPLOYEE_VIEW'),
    ('PAYROLL_ADMIN',   'ATTENDANCE_VIEW'),
    ('PAYROLL_ADMIN',   'LEAVE_VIEW'),

    ('RECRUITER',       'RECRUITMENT_%'),
    ('RECRUITER',       'ORG_VIEW'),

    ('INTERVIEWER',     'RECRUITMENT_VIEW'),
    ('INTERVIEWER',     'INTERVIEW_CONDUCT'),

    ('IT_ADMIN',        'ASSET_%'),
    ('IT_ADMIN',        'EMPLOYEE_VIEW'),
    ('IT_ADMIN',        'USER_VIEW')
) AS grants (role_code, permission_pattern)
JOIN roles r       ON r.code = grants.role_code
JOIN permissions p ON p.code LIKE grants.permission_pattern;
