# Configuración de Swagger para AWS ALB

## URLs de Acceso

### Interfaz de Swagger UI
- **URL Principal**: http://banquito-alb-1166574131.us-east-2.elb.amazonaws.com/swagger-ui.html
- **URL Alternativa**: http://banquito-alb-1166574131.us-east-2.elb.amazonaws.com/swagger-ui/

### Documentación de la API
- **OpenAPI JSON**: http://banquito-alb-1166574131.us-east-2.elb.amazonaws.com/api-docs
- **Configuración de Swagger**: http://banquito-alb-1166574131.us-east-2.elb.amazonaws.com/api-docs/swagger-config

### Endpoint de Salud
- **Health Check**: http://banquito-alb-1166574131.us-east-2.elb.amazonaws.com/api/health

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

### 4. Configuración Unificada
- Se usa solo `application.properties` para toda la configuración
- El Dockerfile no especifica un perfil específico

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

1. **Error 503 Service Temporarily Unavailable**:
   - Verifica que la aplicación esté ejecutándose en el contenedor
   - Revisa los logs del contenedor: `docker logs <container_id>`
   - Confirma que el puerto 80 esté abierto en el ALB

2. **Error "Servicio no encontrado"**:
   - Verifica que las rutas estén configuradas correctamente
   - Confirma que el contexto base sea `/` (no `/api/documentacion`)
   - Prueba el endpoint de salud: `/api/health`

3. **Configuración de Seguridad**:
   - Confirma que las rutas de Swagger estén permitidas en el security config
   - Verifica que `/api/**` esté permitido

4. **Perfil y Configuración**:
   - Revisa los logs de la aplicación para errores de configuración
   - Confirma que `springdoc.swagger-ui.enabled=true`
   - Verifica que la configuración en `application.properties` sea correcta

5. **Verificación de Rutas**:
   - Swagger UI: `/swagger-ui.html`
   - API Docs: `/api-docs`
   - Health Check: `/api/health`
