package com.mycompany.laboratorioapp;

public class ResourceCheck {
    public static void main(String[] args) {
        String name = "fondo para la aplicacion.jpg";
        try {
            java.net.URL url1 = Thread.currentThread().getContextClassLoader().getResource(name);
            java.net.URL url2 = ResourceCheck.class.getResource("/" + name);
            System.out.println("user.dir=" + System.getProperty("user.dir"));
            System.out.println("CL getResource('" + name + "') = " + url1);
            System.out.println("Class.getResource('/" + name + "') = " + url2);

            javax.swing.ImageIcon icon = null;
            if (url1 != null) {
                icon = new javax.swing.ImageIcon(url1);
            }
            if ((icon == null || icon.getIconWidth() <= 0) && url2 != null) {
                icon = new javax.swing.ImageIcon(url2);
            }
            if (icon == null || icon.getIconWidth() <= 0) {
                java.io.File[] tries = new java.io.File[] {
                    new java.io.File("src/main/resources/" + name),
                    new java.io.File("resources/" + name),
                    new java.io.File(name)
                };
                for (java.io.File f : tries) {
                    System.out.println("Trying file: " + f.getAbsolutePath());
                    if (f.exists()) {
                        javax.swing.ImageIcon tryIcon = new javax.swing.ImageIcon(f.getAbsolutePath());
                        if (tryIcon.getIconWidth() > 0) { icon = tryIcon; break; }
                    }
                }
            }

            if (icon != null && icon.getIconWidth() > 0) {
                System.out.println("OK loaded '" + name + "' size=" + icon.getIconWidth() + "x" + icon.getIconHeight());
            } else {
                System.out.println("FAILED to load '" + name + "' via direct lookup");
            }

            javax.swing.ImageIcon fondo = CargarImagen.obtenerFondo();
            if (fondo != null && fondo.getIconWidth() > 0) {
                System.out.println("CargarImagen.obtenerFondo() OK size=" + fondo.getIconWidth() + "x" + fondo.getIconHeight());
                System.exit(0);
            } else {
                System.out.println("CargarImagen.obtenerFondo() FAILED");
                System.exit(1);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(2);
        }
    }
}


