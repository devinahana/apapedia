package com.apapedia.catalog.model.enumerator;

public enum CategoryName {
    Aksesoris_Fashion,
    Buku_dan_Alat_Tulis,
    Elektronik,
    Fashion_Bayi_dan_Anak,
    Hobi_dan_Koleksi,
    Jam_Tangan,
    Perawatan_dan_Kecantikan,
    Makanan_dan_Minuman,
    Otomotif,
    Perlengkapan_Rumah,
    Sovenir_dan_Perlengkapan_Pesta;

    @Override
    public String toString() {
        return name().replace('_', ' ');
    }
}

