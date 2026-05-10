# 🚀 Guía Rápida - Componentes de Formulario AdminLTE

## Acceso Rápido

- **Página de demostración:** http://localhost:5000/showcase
- **Documentación completa:** `src/components/forms/README.md`
- **Ejemplos de código:** `src/components/forms/FormExample.jsx`

## 📦 Instalación

Los componentes ya están instalados en `src/components/forms/`

## 🎯 Uso Rápido

### 1. Importar Componentes

```jsx
import { FormInput, FormCheckbox, FormSelect } from '../components/forms';
```

### 2. Usar en tu Formulario

```jsx
const MiFormulario = () => {
  const [email, setEmail] = useState('');
  const [terminos, setTerminos] = useState(false);

  return (
    <div className="card card-primary">
      <div className="card-header">
        <h3 className="card-title">Mi Formulario</h3>
      </div>
      <form onSubmit={handleSubmit}>
        <div className="card-body">
          <FormInput
            label="Email"
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            icon="fa-envelope"
            required
          />

          <FormCheckbox
            label="Acepto términos"
            id="terminos"
            checked={terminos}
            onChange={(e) => setTerminos(e.target.checked)}
          />
        </div>
        <div className="card-footer">
          <button type="submit" className="btn btn-primary">
            Enviar
          </button>
        </div>
      </form>
    </div>
  );
};
```

## 🎨 Componentes Disponibles

### FormInput
```jsx
<FormInput
  label="Nombre"
  id="nombre"
  placeholder="Tu nombre"
  value={nombre}
  onChange={(e) => setNombre(e.target.value)}
  icon="fa-user"           // Opcional
  error="Campo requerido"  // Opcional
  required
/>
```

### FormCheckbox
```jsx
<FormCheckbox
  label="Recordarme"
  id="remember"
  checked={remember}
  onChange={(e) => setRemember(e.target.checked)}
  custom                   // Opcional: estilo custom
  customColor="primary"    // Opcional
/>
```

### FormRadio
```jsx
<FormRadio
  label="Opción 1"
  id="opt1"
  name="opciones"
  checked={selected === '1'}
  onChange={() => setSelected('1')}
  custom
/>
```

### FormSelect
```jsx
<FormSelect
  label="Rol"
  id="rol"
  value={rol}
  onChange={(e) => setRol(e.target.value)}
  options={[
    { value: '1', label: 'Admin' },
    { value: '2', label: 'User' }
  ]}
  custom
  required
/>
```

### FormTextarea
```jsx
<FormTextarea
  label="Notas"
  id="notas"
  value={notas}
  onChange={(e) => setNotas(e.target.value)}
  rows={4}
/>
```

## 🎨 Clases CSS Útiles

### Cards
```jsx
<div className="card card-primary">      // Azul
<div className="card card-success">      // Verde
<div className="card card-warning">      // Amarillo
<div className="card card-danger">       // Rojo
<div className="card card-info">         // Cyan
<div className="card card-dark">         // Negro

<div className="card card-outline card-primary">  // Solo borde
```

### Botones
```jsx
<button className="btn btn-primary">Primary</button>
<button className="btn btn-success">Success</button>
<button className="btn btn-danger">Danger</button>
<button className="btn btn-warning">Warning</button>
<button className="btn btn-info">Info</button>
<button className="btn btn-default">Default</button>

// Tamaños
<button className="btn btn-primary btn-sm">Pequeño</button>
<button className="btn btn-primary">Normal</button>
<button className="btn btn-primary btn-lg">Grande</button>
<button className="btn btn-primary btn-block">Bloque completo</button>
```

### Iconos (FontAwesome)
```jsx
<i className="fas fa-user"></i>
<i className="fas fa-envelope"></i>
<i className="fas fa-lock"></i>
<i className="fas fa-phone"></i>
<i className="fas fa-save"></i>
<i className="fas fa-check"></i>
<i className="fas fa-times"></i>
<i className="fas fa-trash"></i>
```

## 📝 Ejemplo Completo

```jsx
import { useState } from 'react';
import { FormInput, FormSelect, FormCheckbox } from '../components/forms';

const RegistroUsuario = () => {
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    rol: '',
    activo: false
  });

  const [errors, setErrors] = useState({});

  const roles = [
    { value: 'admin', label: 'Administrador' },
    { value: 'user', label: 'Usuario' }
  ];

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Datos:', formData);
  };

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-md-6">
          <div className="card card-primary">
            <div className="card-header">
              <h3 className="card-title">Registro de Usuario</h3>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="card-body">
                <FormInput
                  label="Nombre Completo"
                  id="nombre"
                  placeholder="Ingresa el nombre"
                  value={formData.nombre}
                  onChange={(e) => setFormData({...formData, nombre: e.target.value})}
                  icon="fa-user"
                  required
                  error={errors.nombre}
                />

                <FormInput
                  label="Email"
                  id="email"
                  type="email"
                  placeholder="ejemplo@correo.com"
                  value={formData.email}
                  onChange={(e) => setFormData({...formData, email: e.target.value})}
                  icon="fa-envelope"
                  required
                  error={errors.email}
                />

                <FormSelect
                  label="Rol"
                  id="rol"
                  value={formData.rol}
                  onChange={(e) => setFormData({...formData, rol: e.target.value})}
                  options={roles}
                  custom
                  required
                />

                <FormCheckbox
                  label="Usuario activo"
                  id="activo"
                  checked={formData.activo}
                  onChange={(e) => setFormData({...formData, activo: e.target.checked})}
                  custom
                  customColor="success"
                />
              </div>
              <div className="card-footer">
                <button type="submit" className="btn btn-primary">
                  <i className="fas fa-save mr-2"></i>
                  Guardar
                </button>
                <button type="button" className="btn btn-default float-right">
                  <i className="fas fa-times mr-2"></i>
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegistroUsuario;
```

## 🔍 Ver en Acción

1. Inicia el servidor: `npm run dev`
2. Abre: http://localhost:5000/showcase
3. Explora todos los componentes y estilos disponibles

## 📚 Recursos Adicionales

- **AdminLTE Docs:** https://adminlte.io/themes/v3/
- **Bootstrap Forms:** https://getbootstrap.com/docs/4.6/components/forms/
- **FontAwesome Icons:** https://fontawesome.com/icons

## ❓ Preguntas Frecuentes

**¿Cómo cambio el color de un card?**
```jsx
// Cambia card-primary por: success, warning, danger, info, dark
<div className="card card-success">
```

**¿Cómo hago un input de solo lectura?**
```jsx
<FormInput ... disabled />
```

**¿Cómo muestro un error de validación?**
```jsx
<FormInput
  ...
  error="Este campo es requerido"
/>
```

**¿Cómo uso iconos en los botones?**
```jsx
<button className="btn btn-primary">
  <i className="fas fa-save mr-2"></i>
  Guardar
</button>
```
