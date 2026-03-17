# Project Map

## Backend
- Main module: `sysware-mainData`
- Java package root: `sysware-mainData/src/main/java/com/sysware/mainData`
- Mapper XML: `sysware-mainData/src/main/resources/mapper/mainData`

## Frontend
- API clients: `sysware-ui/src/api/mainData`
- Pages: `sysware-ui/src/views/mainData`

## SQL
- Seed and schema reference: `qa_system.sql`

## Existing Key Classes
- Token:
  - `config/RemoteDataConfig.java`
  - `service/IRemoteTokenService.java`
  - `service/impl/RemoteTokenServiceImpl.java`
- Remote data:
  - `service/IRemoteDataService.java`
  - `service/impl/RemoteDataServiceImpl.java`
  - `controller/RemoteDataController.java`
- Local CRUD controllers:
  - `controller/HdlOrganizationDepartmentController.java`
  - `controller/HdlPersonBasicInfoController.java`
  - `controller/HdlPersonJobInfoController.java`
- Existing Vue pages:
  - `organizationDepartment/index.vue`
  - `personBasicInfo/index.vue`
  - `personJobInfo/index.vue`

## Existing Remote Endpoints In Config
- Read configured properties in `config/RemoteDataConfig.java`:
  - `orgnizationDepartmentPath`
  - `personBasicPath`
  - `personJobPath`
- Prefer configured properties over hard-coded URLs when extending remote access.

## Scheduler
- `xxl-job` is configured in:
  - `sysware/src/main/resources/application-dev.yml`
  - `sysware/src/main/resources/application-prod.yml`
