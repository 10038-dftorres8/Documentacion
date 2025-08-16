# Configuración de Swagger para AWS ALB

## URLs de Acceso

### Interfaz de Swagger UI
- **URL Principal**: http://banquito-alb-1166574131.us-east-2.elb.amazonaws.com/api/documentacion/swagger-ui.html
- **URL Alternativa**: http://banquito-alb-1166574131.us-east-2.elb.amazonaws.com/api/documentacion/swagger-ui/

### Documentación de la API
- **OpenAPI JSON**: http://banquito-alb-1166574131.us-east-2.elb.amazonaws.com/api/documentacion/api-docs
- **Configuración de Swagger**: http://banquito-alb-1166574131.us-east-2.elb.amazonaws.com/api/documentacion/api-docs/swagger-config

## Configuración Implementada

### 1. Configuración de OpenAPI
- Se agregó el servidor AWS ALB como servidor principal
- Se configuraron múltiples entornos (AWS, Producción, Desarrollo)

### 2. Propiedades de SpringDoc
- `springdoc.swagger-ui.path=/swagger-ui.html`
- `springdoc.api-docs.path=/api-docs`
- `springdoc.swagger-ui.url=/api-docs`
- `springdoc.swagger-ui.config-url=/api-docs/swagger-config`

### 3. Configuración de Seguridad
- Se permitió el acceso público a todas las rutas de Swagger
- Rutas permitidas: `/swagger-ui/**`, `/api-docs/**`, `/v3/api-docs/**`

### 4. Perfil de Producción
- Se creó `application-prod.properties` con configuración optimizada
- El Dockerfile usa el perfil `prod` por defecto

## Características de Swagger UI

- ✅ Try it out habilitado
- ✅ Filtrado de endpoints
- ✅ Deep linking
- ✅ Duración de requests visible
- ✅ Expansión de documentación configurada

## Verificación

Para verificar que la configuración funciona correctamente:

1. Accede a la URL principal de Swagger UI
2. Verifica que los endpoints se muestren correctamente
3. Confirma que el servidor AWS ALB aparezca seleccionado por defecto
4. Prueba la funcionalidad "Try it out" en algún endpoint

## Troubleshooting

Si Swagger no se carga correctamente:

1. Verifica que el puerto 80 esté abierto en el ALB
2. Confirma que las rutas de Swagger estén permitidas en el security config
3. Revisa los logs de la aplicación para errores de configuración
4. Verifica que el perfil `prod` esté activo
