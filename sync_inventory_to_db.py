import psycopg2
import os

# Configuración de la conexión a PostgreSQL
DB_CONFIG = {
    "dbname": "cinema_db",
    "user": "cinema_user",
    "password": "cinema_pass",
    "host": "cinema-postgres",
    "port": 5432,
}

# Obtener la ruta del archivo desde PATH_FILE
INVENTORY_FILE = os.getenv("PATH_FILE", "/app/data/food.txt")

def sync_inventory_to_db():
    if not os.path.exists(INVENTORY_FILE):
        print(f"Archivo {INVENTORY_FILE} no encontrado.")
        return

    with open(INVENTORY_FILE, "r") as file:
        lines = file.readlines()

    conn = psycopg2.connect(**DB_CONFIG)
    cursor = conn.cursor()

    for line in lines:
        parts = line.strip().split(",")
        if len(parts) == 2:  # Formato esperado: producto,cantidad
            producto = parts[0].strip()
            cantidad = int(parts[1].strip())
            cursor.execute(
                """
                INSERT INTO comidas (producto, cantidad)
                VALUES (%s, %s)
                ON CONFLICT (producto) DO UPDATE SET cantidad = EXCLUDED.cantidad;
                """,
                (producto, cantidad),
            )

    conn.commit()
    cursor.close()
    conn.close()
    print("Sincronización de inventario completada.")

if __name__ == "__main__":
    sync_inventory_to_db()

