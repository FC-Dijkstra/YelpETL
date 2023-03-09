PGDMP     :            	        {            yelpdata    14.3    14.3 "    .           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            /           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            0           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            1           1262    16385    yelpdata    DATABASE     ]   CREATE DATABASE yelpdata WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'fr_FR.UTF-8';
    DROP DATABASE yelpdata;
                postgres    false                        2615    245761    etl    SCHEMA        CREATE SCHEMA etl;
    DROP SCHEMA etl;
                postgres    false            2           0    0 
   SCHEMA etl    ACL     !   GRANT ALL ON SCHEMA etl TO yann;
                   postgres    false    4            �            1259    245772    attributscommerces    TABLE     �  CREATE TABLE etl.attributscommerces (
    id integer NOT NULL,
    acceptsinsurance boolean NOT NULL,
    agesallowed boolean NOT NULL,
    alcohol boolean NOT NULL,
    ambience boolean NOT NULL,
    byob boolean NOT NULL,
    byobcorkage boolean NOT NULL,
    bestnights boolean NOT NULL,
    bikeparking boolean NOT NULL,
    businessacceptsbitcoin boolean NOT NULL,
    businessacceptscreditcards boolean NOT NULL,
    businessparking boolean NOT NULL,
    byappointmentonly boolean NOT NULL,
    caters boolean NOT NULL,
    coatcheck boolean NOT NULL,
    corkage boolean NOT NULL,
    dietaryrestrictions boolean NOT NULL,
    dogsallowed boolean NOT NULL,
    drivethru boolean NOT NULL,
    goodfordancing boolean NOT NULL,
    goodforkids boolean NOT NULL,
    goodformeal boolean NOT NULL,
    hairspecializesin boolean NOT NULL,
    happyhour boolean NOT NULL,
    hastv boolean NOT NULL,
    music boolean NOT NULL,
    noiselevel boolean NOT NULL,
    open24hours boolean NOT NULL,
    outdoorseating boolean NOT NULL,
    restaurantsattire boolean NOT NULL,
    restaurantscounterservice boolean NOT NULL,
    restaurantsdelivery boolean NOT NULL,
    restaurantsgoodforgroups boolean NOT NULL,
    restaurantspricerange2 boolean NOT NULL,
    restaurantsreservations boolean NOT NULL,
    restaurantstableservice boolean NOT NULL,
    restaurantstakeout boolean NOT NULL,
    smoking boolean NOT NULL,
    wheelchairaccessible boolean NOT NULL,
    wifi boolean NOT NULL
);
 #   DROP TABLE etl.attributscommerces;
       etl         heap    postgres    false    4            �            1259    245850    avis    TABLE     �   CREATE TABLE etl.avis (
    id integer NOT NULL,
    idutilisateur integer NOT NULL,
    idcommerce integer NOT NULL,
    iddate integer NOT NULL,
    note double precision NOT NULL
);
    DROP TABLE etl.avis;
       etl         heap    postgres    false    4            �            1259    245835    checkins    TABLE     u   CREATE TABLE etl.checkins (
    id integer NOT NULL,
    idcommerce integer NOT NULL,
    iddate integer NOT NULL
);
    DROP TABLE etl.checkins;
       etl         heap    postgres    false    4            �            1259    245777 	   commerces    TABLE     �   CREATE TABLE etl.commerces (
    id integer NOT NULL,
    idlocalisation integer,
    categorie character varying(128),
    idattributs integer NOT NULL,
    stars integer NOT NULL
);
    DROP TABLE etl.commerces;
       etl         heap    postgres    false    4            �            1259    245767    localisation    TABLE     �   CREATE TABLE etl.localisation (
    id integer NOT NULL,
    adresse character varying(200) NOT NULL,
    codepostal character varying(20) NOT NULL,
    ville character varying(50) NOT NULL,
    etat character varying(70) NOT NULL
);
    DROP TABLE etl.localisation;
       etl         heap    postgres    false    4            �            1259    245830    temporalite    TABLE     Y   CREATE TABLE etl.temporalite (
    id integer NOT NULL,
    temporalite date NOT NULL
);
    DROP TABLE etl.temporalite;
       etl         heap    postgres    false    4            �            1259    245795    utilisateurs    TABLE     _  CREATE TABLE etl.utilisateurs (
    id integer NOT NULL,
    nickname character varying(70) NOT NULL,
    datecreation date NOT NULL,
    nbamis integer NOT NULL,
    etoilesmoyennes double precision NOT NULL,
    nbcompliments integer NOT NULL,
    nbfollowers integer NOT NULL,
    nbreactions integer NOT NULL,
    nbanneeelite integer NOT NULL
);
    DROP TABLE etl.utilisateurs;
       etl         heap    postgres    false    4            &          0    245772    attributscommerces 
   TABLE DATA                 etl          postgres    false    217   �)       +          0    245850    avis 
   TABLE DATA                 etl          postgres    false    222   *       *          0    245835    checkins 
   TABLE DATA                 etl          postgres    false    221   &*       '          0    245777 	   commerces 
   TABLE DATA                 etl          postgres    false    218   @*       %          0    245767    localisation 
   TABLE DATA                 etl          postgres    false    216   Z*       )          0    245830    temporalite 
   TABLE DATA                 etl          postgres    false    220   t*       (          0    245795    utilisateurs 
   TABLE DATA                 etl          postgres    false    219   �*       �           2606    245776 *   attributscommerces attributscommerces_pkey 
   CONSTRAINT     e   ALTER TABLE ONLY etl.attributscommerces
    ADD CONSTRAINT attributscommerces_pkey PRIMARY KEY (id);
 Q   ALTER TABLE ONLY etl.attributscommerces DROP CONSTRAINT attributscommerces_pkey;
       etl            postgres    false    217            �           2606    245854    avis avis_pkey 
   CONSTRAINT     I   ALTER TABLE ONLY etl.avis
    ADD CONSTRAINT avis_pkey PRIMARY KEY (id);
 5   ALTER TABLE ONLY etl.avis DROP CONSTRAINT avis_pkey;
       etl            postgres    false    222            �           2606    245839    checkins checkins_pkey 
   CONSTRAINT     Q   ALTER TABLE ONLY etl.checkins
    ADD CONSTRAINT checkins_pkey PRIMARY KEY (id);
 =   ALTER TABLE ONLY etl.checkins DROP CONSTRAINT checkins_pkey;
       etl            postgres    false    221            �           2606    245781    commerces commerces_pkey 
   CONSTRAINT     S   ALTER TABLE ONLY etl.commerces
    ADD CONSTRAINT commerces_pkey PRIMARY KEY (id);
 ?   ALTER TABLE ONLY etl.commerces DROP CONSTRAINT commerces_pkey;
       etl            postgres    false    218            �           2606    245771    localisation localisation_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY etl.localisation
    ADD CONSTRAINT localisation_pkey PRIMARY KEY (id);
 E   ALTER TABLE ONLY etl.localisation DROP CONSTRAINT localisation_pkey;
       etl            postgres    false    216            �           2606    245834    temporalite temporalite_pkey 
   CONSTRAINT     W   ALTER TABLE ONLY etl.temporalite
    ADD CONSTRAINT temporalite_pkey PRIMARY KEY (id);
 C   ALTER TABLE ONLY etl.temporalite DROP CONSTRAINT temporalite_pkey;
       etl            postgres    false    220            �           2606    245799    utilisateurs utilisateurs_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY etl.utilisateurs
    ADD CONSTRAINT utilisateurs_pkey PRIMARY KEY (id);
 E   ALTER TABLE ONLY etl.utilisateurs DROP CONSTRAINT utilisateurs_pkey;
       etl            postgres    false    219            �           2606    245860    avis fk_avis_commerce    FK CONSTRAINT     u   ALTER TABLE ONLY etl.avis
    ADD CONSTRAINT fk_avis_commerce FOREIGN KEY (idcommerce) REFERENCES etl.commerces(id);
 <   ALTER TABLE ONLY etl.avis DROP CONSTRAINT fk_avis_commerce;
       etl          postgres    false    222    4234    218            �           2606    245865    avis fk_avis_temporalite    FK CONSTRAINT     v   ALTER TABLE ONLY etl.avis
    ADD CONSTRAINT fk_avis_temporalite FOREIGN KEY (iddate) REFERENCES etl.temporalite(id);
 ?   ALTER TABLE ONLY etl.avis DROP CONSTRAINT fk_avis_temporalite;
       etl          postgres    false    4238    222    220            �           2606    245855    avis fk_avis_utilisateur    FK CONSTRAINT     ~   ALTER TABLE ONLY etl.avis
    ADD CONSTRAINT fk_avis_utilisateur FOREIGN KEY (idutilisateur) REFERENCES etl.utilisateurs(id);
 ?   ALTER TABLE ONLY etl.avis DROP CONSTRAINT fk_avis_utilisateur;
       etl          postgres    false    4236    222    219            �           2606    245840    checkins fk_checkins_commerce    FK CONSTRAINT     }   ALTER TABLE ONLY etl.checkins
    ADD CONSTRAINT fk_checkins_commerce FOREIGN KEY (idcommerce) REFERENCES etl.commerces(id);
 D   ALTER TABLE ONLY etl.checkins DROP CONSTRAINT fk_checkins_commerce;
       etl          postgres    false    4234    218    221            �           2606    245845     checkins fk_checkins_temporalite    FK CONSTRAINT     ~   ALTER TABLE ONLY etl.checkins
    ADD CONSTRAINT fk_checkins_temporalite FOREIGN KEY (iddate) REFERENCES etl.temporalite(id);
 G   ALTER TABLE ONLY etl.checkins DROP CONSTRAINT fk_checkins_temporalite;
       etl          postgres    false    4238    220    221            �           2606    245782     commerces fk_commerces_attributs    FK CONSTRAINT     �   ALTER TABLE ONLY etl.commerces
    ADD CONSTRAINT fk_commerces_attributs FOREIGN KEY (idattributs) REFERENCES etl.attributscommerces(id);
 G   ALTER TABLE ONLY etl.commerces DROP CONSTRAINT fk_commerces_attributs;
       etl          postgres    false    217    4232    218            �           2606    245787 #   commerces fk_commerces_localisation    FK CONSTRAINT     �   ALTER TABLE ONLY etl.commerces
    ADD CONSTRAINT fk_commerces_localisation FOREIGN KEY (idlocalisation) REFERENCES etl.localisation(id);
 J   ALTER TABLE ONLY etl.commerces DROP CONSTRAINT fk_commerces_localisation;
       etl          postgres    false    216    4230    218            &   
   x���          +   
   x���          *   
   x���          '   
   x���          %   
   x���          )   
   x���          (   
   x���         