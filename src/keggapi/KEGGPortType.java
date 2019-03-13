/**
 * KEGGPortType.java
 *
 * このファイルはWSDLから自動生成されました / [en]-(This file was auto-generated from WSDL)
 * Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java生成器によって / [en]-(by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.)
 */

package keggapi;

public interface KEGGPortType extends java.rmi.Remote {
    public keggapi.Definition[] list_databases() throws java.rmi.RemoteException;
    public keggapi.Definition[] list_organisms() throws java.rmi.RemoteException;
    public keggapi.Definition[] list_pathways(java.lang.String org) throws java.rmi.RemoteException;
    public keggapi.Definition[] list_ko_classes(java.lang.String class_id) throws java.rmi.RemoteException;
    public java.lang.String binfo(java.lang.String db) throws java.rmi.RemoteException;
    public java.lang.String bget(java.lang.String string) throws java.rmi.RemoteException;
    public java.lang.String bfind(java.lang.String string) throws java.rmi.RemoteException;
    public java.lang.String btit(java.lang.String string) throws java.rmi.RemoteException;
    public java.lang.String bconv(java.lang.String string) throws java.rmi.RemoteException;
    public keggapi.LinkDBRelation[] get_linkdb_by_entry(java.lang.String entry_id, java.lang.String db, int offset, int limit) throws java.rmi.RemoteException;
    public keggapi.SSDBRelation[] get_best_neighbors_by_gene(java.lang.String genes_id, int offset, int limit) throws java.rmi.RemoteException;
    public keggapi.SSDBRelation[] get_best_best_neighbors_by_gene(java.lang.String genes_id, int offset, int limit) throws java.rmi.RemoteException;
    public keggapi.SSDBRelation[] get_reverse_best_neighbors_by_gene(java.lang.String genes_id, int offset, int limit) throws java.rmi.RemoteException;
    public keggapi.SSDBRelation[] get_paralogs_by_gene(java.lang.String genes_id, int offset, int limit) throws java.rmi.RemoteException;
    public keggapi.MotifResult[] get_motifs_by_gene(java.lang.String genes_id, java.lang.String db) throws java.rmi.RemoteException;
    public keggapi.Definition[] get_genes_by_motifs(java.lang.String[] motif_id_list, int offset, int limit) throws java.rmi.RemoteException;
    public java.lang.String[] get_ko_by_gene(java.lang.String genes_id) throws java.rmi.RemoteException;
    public keggapi.Definition[] get_ko_by_ko_class(java.lang.String class_id) throws java.rmi.RemoteException;
    public keggapi.Definition[] get_genes_by_ko(java.lang.String ko_id, java.lang.String org) throws java.rmi.RemoteException;
    public keggapi.Definition[] get_genes_by_ko_class(java.lang.String class_id, java.lang.String org, int offset, int limit) throws java.rmi.RemoteException;
    public keggapi.PathwayElement[] get_elements_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public keggapi.PathwayElementRelation[] get_element_relations_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String color_pathway_by_elements(java.lang.String pathway_id, int[] element_list, java.lang.String[] fg_color_list, java.lang.String[] bg_color_list) throws java.rmi.RemoteException;
    public java.lang.String get_html_of_colored_pathway_by_elements(java.lang.String pathway_id, int[] element_list, java.lang.String[] fg_color_list, java.lang.String[] bg_color_list) throws java.rmi.RemoteException;
    public java.lang.String mark_pathway_by_objects(java.lang.String pathway_id, java.lang.String[] object_id_list) throws java.rmi.RemoteException;
    public java.lang.String color_pathway_by_objects(java.lang.String pathway_id, java.lang.String[] object_id_list, java.lang.String[] fg_color_list, java.lang.String[] bg_color_list) throws java.rmi.RemoteException;
    public java.lang.String get_html_of_marked_pathway_by_objects(java.lang.String pathway_id, java.lang.String[] object_id_list) throws java.rmi.RemoteException;
    public java.lang.String get_html_of_colored_pathway_by_objects(java.lang.String pathway_id, java.lang.String[] object_id_list, java.lang.String[] fg_color_list, java.lang.String[] bg_color_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_genes_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_reactions_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_genes(java.lang.String[] genes_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_enzymes(java.lang.String[] enzyme_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_reactions(java.lang.String[] reaction_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_linked_pathways(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_genes_by_enzyme(java.lang.String enzyme_id, java.lang.String org) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_gene(java.lang.String genes_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_reaction(java.lang.String reaction_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_reactions_by_enzyme(java.lang.String enzyme_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_genes_by_organism(java.lang.String org, int offset, int limit) throws java.rmi.RemoteException;
    public int get_number_of_genes_by_organism(java.lang.String abbr) throws java.rmi.RemoteException;
    public java.lang.String[] get_reactions_by_glycan(java.lang.String glycan_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_reactions_by_compound(java.lang.String compound_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_glycan(java.lang.String glycan_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_enzymes_by_compound(java.lang.String compound_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_compounds(java.lang.String[] compound_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_glycans(java.lang.String[] glycan_id_list) throws java.rmi.RemoteException;
    public java.lang.String[] get_compounds_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_glycans_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_compounds_by_reaction(java.lang.String reaction_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_glycans_by_reaction(java.lang.String reaction_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_compounds_by_enzyme(java.lang.String enzyme_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_glycans_by_enzyme(java.lang.String enzyme_id) throws java.rmi.RemoteException;
    public java.lang.String convert_mol_to_kcf(java.lang.String mol_text) throws java.rmi.RemoteException;
    public java.lang.String[] get_kos_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_kos(java.lang.String[] ko_id_list, java.lang.String org) throws java.rmi.RemoteException;
    public java.lang.String[] search_compounds_by_name(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String[] search_glycans_by_name(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String[] search_compounds_by_composition(java.lang.String composition) throws java.rmi.RemoteException;
    public java.lang.String[] search_compounds_by_mass(float mass, float range) throws java.rmi.RemoteException;
    public java.lang.String[] search_glycans_by_mass(float mass, float range) throws java.rmi.RemoteException;
    public java.lang.String[] search_glycans_by_composition(java.lang.String composition) throws java.rmi.RemoteException;
    public keggapi.StructureAlignment[] search_compounds_by_subcomp(java.lang.String mol, int offset, int limit) throws java.rmi.RemoteException;
    public keggapi.StructureAlignment[] search_glycans_by_kcam(java.lang.String kcf, java.lang.String program, java.lang.String option, int offset, int limit) throws java.rmi.RemoteException;
    public keggapi.LinkDBRelation[] get_linkdb_between_databases(java.lang.String from_db, java.lang.String to_db, int offset, int limit) throws java.rmi.RemoteException;
    public java.lang.String[] search_drugs_by_name(java.lang.String name) throws java.rmi.RemoteException;
    public java.lang.String[] search_drugs_by_composition(java.lang.String composition) throws java.rmi.RemoteException;
    public java.lang.String[] search_drugs_by_mass(float mass, float range) throws java.rmi.RemoteException;
    public keggapi.StructureAlignment[] search_drugs_by_subcomp(java.lang.String mol, int offset, int limit) throws java.rmi.RemoteException;
    public int[] get_references_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_drugs_by_pathway(java.lang.String pathway_id) throws java.rmi.RemoteException;
    public java.lang.String[] get_pathways_by_drugs(java.lang.String[] drug_id_list) throws java.rmi.RemoteException;
}
